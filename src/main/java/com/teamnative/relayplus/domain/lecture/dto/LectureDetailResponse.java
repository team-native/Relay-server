package com.teamnative.relayplus.domain.lecture.dto;

import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;

import java.time.LocalDateTime;

/**
 * 강의 상세 조회용 응답 DTO
 */
public record LectureDetailResponse(
        Long id,
        String title,
        String speaker,
        LocalDateTime lectureAt,
        Integer capacity,
        String description,
        LectureStatus status,
        String authorName,
        LocalDateTime createdAt
) {
    public static LectureDetailResponse from(Lecture lecture) {
        return new LectureDetailResponse(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getSpeaker(),
                lecture.getLectureAt(),
                lecture.getCapacity(),
                lecture.getDescription(),
                lecture.getStatus(),
                lecture.getAuthor().getName(),
                lecture.getCreatedAt()
        );
    }
}
