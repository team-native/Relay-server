package com.teamnative.relayplus.domain.enrollment.dto;

import com.teamnative.relayplus.domain.enrollment.entity.Enrollment;

import java.time.LocalDateTime;

/**
 * 강의 신청 응답 DTO
 */
public record EnrollmentResponse(
        Long enrollmentId,
        Long lectureId,
        String lectureTitle,
        Long userId,
        LocalDateTime enrolledAt
) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getLecture().getId(),
                enrollment.getLecture().getTitle(),
                enrollment.getUser().getId(),
                enrollment.getCreatedAt()
        );
    }
}
