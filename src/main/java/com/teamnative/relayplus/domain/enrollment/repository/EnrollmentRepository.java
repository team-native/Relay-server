package com.teamnative.relayplus.domain.enrollment.repository;

import com.teamnative.relayplus.domain.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 강의 신청(Enrollment)의 리포지토리입니다.
 */
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * 특정 강의에 대한 사용자의 신청 조회
     * (중복 신청 방지용)
     */
    Optional<Enrollment> findByLectureIdAndUserId(Long lectureId, Long userId);

    /**
     * 사용자의 모든 신청 조회
     */
    List<Enrollment> findByUserId(Long userId);

    /**
     * 특정 강의의 신청 인원 수 조회
     */
    long countByLectureId(Long lectureId);
}
