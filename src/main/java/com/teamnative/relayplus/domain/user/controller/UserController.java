package com.teamnative.relayplus.domain.user.controller;

import com.teamnative.relayplus.domain.user.dto.PasswordChangeRequest;
import com.teamnative.relayplus.domain.user.dto.UserEditRequest;
import com.teamnative.relayplus.domain.user.dto.UserResponse;
import com.teamnative.relayplus.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 정보 관리 API
 * - GET /api/users/myPage              : 마이페이지 조회
 * - PATCH /api/users/myPage/profile    : 프로필 수정 (name, generation, department)
 * - PATCH /api/users/myPage/password   : 비밀번호 변경
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 마이페이지 조회
     * 현재 로그인한 사용자의 프로필 정보를 반환합니다.
     */
    @GetMapping("/myPage")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        UserResponse response = userService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 수정
     * name, generation, department를 수정합니다.
     */
    @PatchMapping("/myPage/profile")
    public ResponseEntity<UserResponse> editProfile(
            Authentication authentication,
            @Valid @RequestBody UserEditRequest request
    ) {
        UserResponse response = userService.editProfile(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 변경
     * 현재 비밀번호를 검증 후 새 비밀번호로 변경합니다.
     */
    @PatchMapping("/myPage/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request
    ) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }
}
