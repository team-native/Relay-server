package com.teamnative.relayplus.domain.user.dto;

import com.teamnative.relayplus.domain.auth.entity.Department;
import com.teamnative.relayplus.domain.auth.entity.Generation;
import com.teamnative.relayplus.domain.user.entity.UserProfileEntity;

/**
 * 마이페이지 조회 및 프로필 수정 후 반환하는 DTO입니다.
 */
public record UserResponse(
        Long userId,
        String name,
        Generation generation,
        Department department
) {
    public static UserResponse from(UserProfileEntity user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getGeneration(),
                user.getDepartment()
        );
    }
}
