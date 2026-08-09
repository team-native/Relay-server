package com.teamnative.relayplus.domain.lecture.dto;

import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;

import java.time.LocalDateTime;

/**
 * 메인페이지 / 상태별 목록 조회용 응답 DTO (목록에서는 연사 소개까지는 노출하지 않습니다)
 * speaker는 별도 컬럼이 아니라 등록자(author)의 이름을 그대로 내려줍니다.
 */
public record LectureSummaryResponse(
        Long id,
        String title,
        String speaker,
        LocalDateTime lectureAt,
        Integer capacity,
        LectureStatus status
) {
    public static LectureSummaryResponse from(Lecture lecture) {
        return new LectureSummaryResponse(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getAuthor().getName(),
                lecture.getLectureAt(),
                lecture.getCapacity(),
                lecture.getStatus()
        );
    }
}
