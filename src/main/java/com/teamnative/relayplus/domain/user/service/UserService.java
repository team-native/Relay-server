package com.teamnative.relayplus.domain.user.service;

import com.teamnative.relayplus.domain.enrollment.repository.EnrollmentRepository;
import com.teamnative.relayplus.domain.lecture.dto.LectureSummaryResponse;
import com.teamnative.relayplus.domain.user.dto.PasswordChangeRequest;
import com.teamnative.relayplus.domain.user.dto.UserEditRequest;
import com.teamnative.relayplus.domain.user.dto.UserResponse;
import com.teamnative.relayplus.domain.user.entity.UserProfileEntity;
import com.teamnative.relayplus.domain.user.repository.UserProfileRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사용자 정보 관리 서비스
 * - 마이페이지 조회
 * - 프로필 수정 (name, generation, department)
 * - 비밀번호 변경
 * user/User(프로필)와 auth/User(계정·비밀번호)는 테이블은 같지만 서로 다른 엔티티이므로,
 * 비밀번호가 관련된 로직에서는 반드시 auth 쪽 UserRepository/User를 사용합니다.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserProfileRepository userRepository;
    private final com.teamnative.relayplus.domain.auth.repository.UserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EnrollmentRepository enrollmentRepository;

    public UserService(
            UserProfileRepository userRepository,
            com.teamnative.relayplus.domain.auth.repository.UserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            EnrollmentRepository enrollmentRepository
    ) {
        this.userRepository = userRepository;
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * JWT principal(email)로 실제 userId(PK)를 조회합니다.
     * 토큰에는 email만 담겨 있으므로, id 기반 조회가 필요한 곳에서는
     * 이 메서드로 먼저 본인의 userId를 확인한 뒤 사용합니다.
     */
    private Long resolveUserId(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getId();
    }

    /**
     * 마이페이지 조회
     * user/User 정보와 함께 사용자가 신청한 강의 목록을 반환합니다.
     */
    public UserResponse getUserProfile(String email) {
        Long userId = resolveUserId(email);
        UserProfileEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사용자가 신청한 강의 목록 조회
        List<LectureSummaryResponse> enrolledLectures = enrollmentRepository.findByUserId(userId)
                .stream()
                .map(enrollment -> LectureSummaryResponse.from(enrollment.getLecture()))
                .toList();

        return UserResponse.from(user, enrolledLectures);
    }

    /**
     * 프로필 수정 (name, generation, department)
     */
    @Transactional
    public UserResponse editProfile(String email, UserEditRequest request) {
        Long userId = resolveUserId(email);
        UserProfileEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateProfile(request.name(), request.generation(), request.department());
        userRepository.save(user);

        // 프로필 수정 후 내가 들은 강의 목록도 함께 반환
        List<LectureSummaryResponse> enrolledLectures = enrollmentRepository.findByUserId(userId)
                .stream()
                .map(enrollment -> LectureSummaryResponse.from(enrollment.getLecture()))
                .toList();

        return UserResponse.from(user, enrolledLectures);
    }

    /**
     * 비밀번호 변경
     * 1) 현재 비밀번호 검증 (auth/User에서)
     * 2) 새 비밀번호 == 새 비밀번호 확인 검증
     * 3) 새 비밀번호로 암호화해서 저장 (auth/User에서)
     */
    @Transactional
    public void changePassword(String email, PasswordChangeRequest request) {
        com.teamnative.relayplus.domain.auth.entity.User authUser = authUserRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.currentPassword(), authUser.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 확인 일치 검증
        if (!request.newPassword().equals(request.newPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 새 비밀번호로 변경 (암호화)
        authUser.changePassword(passwordEncoder.encode(request.newPassword()));
        authUserRepository.save(authUser);
    }
}