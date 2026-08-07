package com.teamnative.relayplus.domain.auth.dto;

import com.teamnative.relayplus.domain.auth.entity.Department;
import com.teamnative.relayplus.domain.auth.entity.Generation;
import com.teamnative.relayplus.domain.auth.service.AuthPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest(

        @NotBlank(message = "이름을 입력해주세요.")
        @Size(max = 30, message = "이름은 30자 이하로 입력해주세요.")
        String name,

        @NotBlank(message = "학교 이메일을 입력해주세요.")
        @Pattern(
                regexp = AuthPattern.SCHOOL_EMAIL,
                message = "학교 이메일 형식이 올바르지 않습니다. (예: s00000@gsm.hs.kr)"
        )
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(
                regexp = AuthPattern.PASSWORD,
                message = "비밀번호는 영문과 숫자를 포함하여 8자 이상 64자 이하여야 합니다."
        )
        String password,

        @NotBlank(message = "비밀번호 확인을 입력해주세요.")
        String passwordConfirm,

        @NotNull(message = "기수를 선택해주세요.")
        Generation generation,

        @NotNull(message = "학과를 선택해주세요.")
        Department department
) {
}
