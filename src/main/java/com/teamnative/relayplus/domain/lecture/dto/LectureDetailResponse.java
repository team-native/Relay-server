package com.teamnative.relayplus.domain.lecture.dto;

import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;

import java.time.LocalDateTime;

/**
 * 강의 상세 조회용 응답 DTO
 * presenter는 등록 요청에서 입력한 연사자 이름을 내려줍니다.
 * enrolledCount는 현재까지의 신청 인원 수, enrolled는 요청한(로그인한) 사용자의 신청 여부입니다.
 */
public record LectureDetailResponse(
        Long id,
        String title,
        String presenter,
        LocalDateTime scheduledAt,
        Integer capacity,
        String description,
        LectureStatus status,
        LocalDateTime createdAt,
        long enrolledCount,
        boolean enrolled
) {
    public static LectureDetailResponse from(Lecture lecture, long enrolledCount, boolean enrolled) {
        return new LectureDetailResponse(
                lecture.getId(),
                lecture.getTitle(),
                lecture.getPresenter(),
                lecture.getScheduledAt(),
                lecture.getCapacity(),
                lecture.getDescription(),
                lecture.getStatus(),
                lecture.getCreatedAt(),
                enrolledCount,
                enrolled
        );
    }
}
