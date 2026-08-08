package com.teamnative.relayplus.domain.enrollment.service;

import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.domain.enrollment.dto.EnrollmentCreateRequest;
import com.teamnative.relayplus.domain.enrollment.dto.EnrollmentResponse;
import com.teamnative.relayplus.domain.enrollment.entity.Enrollment;
import com.teamnative.relayplus.domain.enrollment.repository.EnrollmentRepository;
import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.repository.LectureRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 강의 신청(Enrollment) 서비스
 *
 * 기능:
 * - 강의 신청 (자동으로 신청 완료)
 * - 신청 취소 (삭제)
 */
@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    public EnrollmentService(
            EnrollmentRepository enrollmentRepository,
            LectureRepository lectureRepository,
            UserRepository userRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    /**
     * 강의 신청
     *
     * 검증:
     * 1. 강의 존재 여부
     * 2. 사용자 존재 여부
     * 3. 중복 신청 여부
     *
     * 신청하면 자동으로 신청 완료 상태입니다.
     */
    @Transactional
    public EnrollmentResponse createEnrollment(String email, EnrollmentCreateRequest request) {
        // 1. 강의 존재 확인
        Lecture lecture = lectureRepository.findById(request.lectureId())
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        // 2. 사용자 존재 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 3. 중복 신청 확인
        enrollmentRepository.findByLectureIdAndUserId(request.lectureId(), user.getId())
                .ifPresent(enrollment -> {
                    throw new CustomException(ErrorCode.ALREADY_ENROLLED);
                });

        // 신청 생성 (자동으로 신청 완료)
        Enrollment enrollment = Enrollment.builder()
                .lecture(lecture)
                .user(user)
                .build();

        enrollmentRepository.save(enrollment);
        return EnrollmentResponse.from(enrollment);
    }

    /**
     * 신청 취소
     */
    @Transactional
    public void cancelEnrollment(String email, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENROLLMENT_NOT_FOUND));

        // 본인 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!enrollment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 신청 취소 (삭제)
        enrollmentRepository.deleteById(enrollmentId);
    }
}
