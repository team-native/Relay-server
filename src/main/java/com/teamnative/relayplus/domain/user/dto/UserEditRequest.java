package com.teamnative.relayplus.domain.user.dto;

import com.teamnative.relayplus.domain.auth.entity.Department;
import com.teamnative.relayplus.domain.auth.entity.Generation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 프로필 수정 요청 DTO
 */
public record UserEditRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 30, message = "이름은 30자 이하로 입력해주세요.")
        String name,

        @NotNull(message = "기수를 선택해주세요.")
        Generation generation,

        @NotNull(message = "학과를 선택해주세요.")
        Department department
) {
}
