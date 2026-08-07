package com.teamnative.relayplus.domain.auth.dto;

import com.teamnative.relayplus.domain.auth.entity.User;

public record LoginResponse(
        Long userId,
        String name,
        String email,
        String role,
        TokenResponse token
) {
    public static LoginResponse of(User user, TokenResponse token) {
        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                token
        );
    }
}
