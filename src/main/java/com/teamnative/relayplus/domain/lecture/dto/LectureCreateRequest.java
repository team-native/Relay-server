package com.teamnative.relayplus.domain.lecture.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 릴레이 스터디 등록 요청 DTO
 * 연사자는 별도 입력 없이 등록한 로그인 사용자(author)로 자동 설정됩니다.
 */
public record LectureCreateRequest(

        @NotBlank(message = "연사 제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
        String title,

        @NotNull(message = "연사 날짜와 시간을 입력해주세요.")
        @Future(message = "연사 날짜와 시간은 현재보다 이후여야 합니다.")
        LocalDateTime lectureAt,

        @NotNull(message = "모집 인원을 입력해주세요.")
        @Positive(message = "모집 인원은 1명 이상이어야 합니다.")
        Integer capacity,

        @NotBlank(message = "연사 소개를 입력해주세요.")
        String description
) {
}
