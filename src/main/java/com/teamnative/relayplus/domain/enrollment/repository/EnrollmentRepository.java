package com.teamnative.relayplus.domain.enrollment.repository;

import com.teamnative.relayplus.domain.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * 여러 강의의 신청 인원 수를 한 번에 조회합니다. (목록 조회 시 N+1 방지)
     * 결과 각 행은 Object[]{ lectureId(Long), count(Long) } 입니다.
     */
    @Query("SELECT e.lecture.id, COUNT(e) FROM Enrollment e WHERE e.lecture.id IN :lectureIds GROUP BY e.lecture.id")
    List<Object[]> countByLectureIdIn(@Param("lectureIds") List<Long> lectureIds);

    /**
     * 특정 사용자가 주어진 강의 목록 중 신청한 내역만 조회합니다.
     * (목록 조회 시 로그인한 사용자의 신청 여부 표시용, N+1 방지)
     */
    List<Enrollment> findByUserIdAndLectureIdIn(Long userId, List<Long> lectureIds);

    void deleteByLectureId(Long lectureId);
}
