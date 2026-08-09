package com.teamnative.relayplus.domain.enrollment.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 강의 신청 요청 DTO
 */
public record EnrollmentCreateRequest(
        @NotNull(message = "강의 ID를 입력해주세요.")
        Long lectureId
) {
}
