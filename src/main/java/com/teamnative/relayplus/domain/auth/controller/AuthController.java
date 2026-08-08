package com.teamnative.relayplus.domain.auth.controller;

import com.teamnative.relayplus.domain.auth.dto.LoginRequest;
import com.teamnative.relayplus.domain.auth.dto.LoginResponse;
import com.teamnative.relayplus.domain.auth.dto.SignupRequest;
import com.teamnative.relayplus.domain.auth.dto.SignupResponse;
import com.teamnative.relayplus.domain.auth.dto.TokenRefreshRequest;
import com.teamnative.relayplus.domain.auth.dto.TokenResponse;
import com.teamnative.relayplus.domain.auth.service.AuthService;
import com.teamnative.relayplus.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 API
 * - POST /api/auth/signup  : 회원가입
 * - POST /api/auth/login   : 로그인 (Access/Refresh Token 발급)
 * - POST /api/auth/reissue : Refresh Token으로 Access Token 재발급
 * - POST /api/auth/logout  : 로그아웃 (저장된 Refresh Token 폐기, Access Token 필요)
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity
                .ok(ApiResponse.success("로그인에 성공했습니다.", response));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody TokenRefreshRequest request) {
        TokenResponse response = authService.reissue(request);
        return ResponseEntity
                .ok(ApiResponse.success("토큰이 재발급되었습니다.", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity
                .ok(ApiResponse.success("로그아웃 되었습니다.", null));
    }
}
