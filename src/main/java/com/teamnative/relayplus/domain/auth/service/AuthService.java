package com.teamnative.relayplus.domain.auth.service;

import com.teamnative.relayplus.domain.auth.dto.LoginRequest;
import com.teamnative.relayplus.domain.auth.dto.LoginResponse;
import com.teamnative.relayplus.domain.auth.dto.SignupRequest;
import com.teamnative.relayplus.domain.auth.dto.SignupResponse;
import com.teamnative.relayplus.domain.auth.dto.TokenRefreshRequest;
import com.teamnative.relayplus.domain.auth.dto.TokenResponse;
import com.teamnative.relayplus.domain.auth.entity.RefreshToken;
import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.auth.repository.RefreshTokenRepository;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import com.teamnative.relayplus.global.jwt.JwtTokenProvider;
import com.teamnative.relayplus.global.security.TokenHasher;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailVerificationService emailVerificationService;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            EmailVerificationService emailVerificationService
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailVerificationService = emailVerificationService;
    }

    /**
     * 회원가입
     * 1) 비밀번호 / 비밀번호 확인 일치 여부 확인
     * 2) 이메일 중복 확인
     * 3) 비밀번호 암호화 후 저장
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (!request.password().equals(request.passwordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        emailVerificationService.assertVerified(request.email());

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .generation(request.generation())
                .department(request.department())
                .build();

        User savedUser = userRepository.save(user);

        emailVerificationService.consume(request.email());

        return SignupResponse.from(savedUser);
    }

    /**
     * 로그인
     * 이메일로 사용자를 조회 후 비밀번호를 검증하고, 성공 시 Access/Refresh Token을 발급합니다.
     * 보안을 위해 이메일이 없는 경우와 비밀번호가 틀린 경우 동일한 에러 메시지를 반환합니다.
     * 토큰과 함께 로그인 직후 화면에 필요한 사용자 정보를 LoginResponse로 묶어 반환합니다.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        TokenResponse token = issueTokens(user);

        return LoginResponse.of(user, token);
    }

    /**
     * Access Token 재발급
     * 1) Refresh Token 자체의 서명/만료/타입(category) 검증
     * 2) DB에 저장된 값과 일치하는지 확인 (탈취/재사용 방지)
     * 3) 통과 시 Access/Refresh Token을 모두 새로 발급 (Refresh Token Rotation)
     *
     * [수정사항]
     * - Refresh Token 파싱 중 발생할 수 있는 JwtException 처리 추가
     * - 토큰 파싱 실패 시 INVALID_TOKEN 또는 EXPIRED_TOKEN으로 처리
     * - 서버 500 에러 대신 401 Unauthorized 응답
     */
    @Transactional
    public TokenResponse reissue(TokenRefreshRequest request) {
        String refreshToken = request.refreshToken();

        try {
            JwtTokenProvider.TokenStatus status = jwtTokenProvider.validate(refreshToken);
            if (status == JwtTokenProvider.TokenStatus.EXPIRED) {
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            }
            if (status == JwtTokenProvider.TokenStatus.INVALID || !jwtTokenProvider.isRefreshToken(refreshToken)) {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            String email = jwtTokenProvider.getEmail(refreshToken);

            RefreshToken savedToken = refreshTokenRepository.findById(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

            if (savedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                refreshTokenRepository.deleteById(email);
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            }

            // 클라이언트가 보낸 토큰이 DB에 저장된 최신 토큰과 다르면
            // 이미 사용된(탈취되었을 수 있는) 토큰이므로 즉시 폐기하고 재로그인을 요구합니다.
            // DB에는 원문이 아닌 SHA-256 해시만 저장되어 있으므로, 들어온 토큰도 동일하게 해시해 비교합니다.
            if (!savedToken.getTokenHash().equals(TokenHasher.sha256(refreshToken))) {
                refreshTokenRepository.deleteById(email);
                throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            return issueTokens(user);
        } catch (JwtException e) {
            // JWT 파싱 중 예상치 못한 예외 발생 시 INVALID_TOKEN으로 처리
            // GlobalExceptionHandler가 이를 401 Unauthorized로 변환
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 로그아웃
     * 저장된 Refresh Token을 삭제하여 이후 재발급 요청을 무효화합니다.
     */
    @Transactional
    public void logout(String email) {
        refreshTokenRepository.deleteById(email);
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        saveOrUpdateRefreshToken(user, refreshToken);

        return TokenResponse.of(accessToken, refreshToken);
    }

    private void saveOrUpdateRefreshToken(User user, String refreshToken) {
        LocalDateTime expiryDate = LocalDateTime.now()
                .plus(Duration.ofMillis(jwtTokenProvider.getRefreshExpirationMillis()));
        String tokenHash = TokenHasher.sha256(refreshToken);

        refreshTokenRepository.findById(user.getEmail())
                .ifPresentOrElse(
                        existing -> existing.update(tokenHash, expiryDate),
                        () -> refreshTokenRepository.save(
                                new RefreshToken(user.getEmail(), user.getId(), tokenHash, expiryDate)
                        )
                );
    }
}
