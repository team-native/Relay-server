package com.teamnative.relayplus.domain.lecture.dto;

import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;

import java.time.LocalDateTime;

/**
 * 강의 상세 조회용 응답 DTO
 * speaker는 별도 컬럼이 아니라 등록자(author)의 이름을 그대로 내려줍니다 (등록자 = 연사자).
 */
public record LectureDetailResponse(
        Long id,
        String title,
        String speaker,
        LocalDateTime lectureAt,
        Integer capacity,
        String description,
        LectureStatus status,
        LocalDateTime createdAt
) {
    public static LectureDetailResponse from(Lecture lecture) {
        return new LectureDetailResponse(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getAuthor().getName(),
                lecture.getLectureAt(),
                lecture.getCapacity(),
                lecture.getDescription(),
                lecture.getStatus(),
                lecture.getCreatedAt()
        );
    }
}
