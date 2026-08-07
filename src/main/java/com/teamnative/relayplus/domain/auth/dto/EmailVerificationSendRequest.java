package com.teamnative.relayplus.domain.auth.dto;

import com.teamnative.relayplus.domain.auth.service.AuthPattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationSendRequest(

        @NotBlank(message = "학교 이메일을 입력해주세요.")
        @Pattern(
                regexp = AuthPattern.SCHOOL_EMAIL,
                message = "학교 이메일 형식이 올바르지 않습니다. (예: s00000@gsm.hs.kr)"
        )
        String email
) {
}
