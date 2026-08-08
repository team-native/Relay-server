package com.teamnative.relayplus.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Access Token 재발급 요청 DTO
 */
public record TokenRefreshRequest(
        @NotBlank(message = "Refresh Token을 입력해주세요.")
        String refreshToken
) {
}
