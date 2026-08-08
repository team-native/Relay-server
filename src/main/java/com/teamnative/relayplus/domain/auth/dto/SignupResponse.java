package com.teamnative.relayplus.domain.auth.dto;

import com.teamnative.relayplus.domain.auth.entity.User;

public record SignupResponse(
        Long userId,
        String name,
        String email
) {
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getName(), user.getEmail());
    }
}
