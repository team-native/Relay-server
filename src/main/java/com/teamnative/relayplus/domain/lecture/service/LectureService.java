package com.teamnative.relayplus.domain.lecture.service;

import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.domain.enrollment.repository.EnrollmentRepository;
import com.teamnative.relayplus.domain.lecture.dto.LectureCreateRequest;
import com.teamnative.relayplus.domain.lecture.dto.LectureDetailResponse;
import com.teamnative.relayplus.domain.lecture.dto.LectureSummaryResponse;
import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;
import com.teamnative.relayplus.domain.lecture.repository.LectureRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public LectureService(LectureRepository lectureRepository, UserRepository userRepository,
                          EnrollmentRepository enrollmentRepository) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * 릴레이 스터디 등록. 로그인한 사용자(email)를 작성자로 저장합니다.
     * 등록 직후 상태는 항상 PENDING(개설미정)입니다.
     */
    @Transactional
    public LectureDetailResponse create(LectureCreateRequest request, String email) {
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = Lecture.builder()
                .title(request.title())
                .presenter(request.presenter())
                .scheduledAt(request.scheduledAt())
                .capacity(request.capacity())
                .description(request.description())
                .author(author)
                .build();

        Lecture saved = lectureRepository.save(lecture);
        return LectureDetailResponse.from(saved, 0L, false);
    }

    /**
     * 메인페이지 / 개설미정 / 개설확정 / 종료 목록 조회를 겸합니다.
     * status가 null이면 전체, keyword가 있으면 제목 검색까지 적용됩니다.
     * 각 강의의 신청 인원 수(enrolledCount)와 요청한 사용자의 신청 여부(enrolled)를 함께 내려줍니다.
     */
    public List<LectureSummaryResponse> getList(LectureStatus status, String keyword, String email) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        List<Lecture> lectures;
        if (status != null && hasKeyword) {
            lectures = lectureRepository.findByStatusAndTitleContainingOrderByCreatedAtDesc(status, keyword);
        } else if (status != null) {
            lectures = lectureRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (hasKeyword) {
            lectures = lectureRepository.findByTitleContainingOrderByCreatedAtDesc(keyword);
        } else {
            lectures = lectureRepository.findAllByOrderByCreatedAtDesc();
        }

        if (lectures.isEmpty()) {
            return List.of();
        }

        List<Long> lectureIds = lectures.stream().map(Lecture::getId).toList();

        Map<Long, Long> enrolledCountMap = enrollmentRepository.countByLectureIdIn(lectureIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        Set<Long> enrolledLectureIds = resolveEnrolledLectureIds(email, lectureIds);

        return lectures.stream()
                .map(lecture -> LectureSummaryResponse.from(
                        lecture,
                        enrolledCountMap.getOrDefault(lecture.getId(), 0L),
                        enrolledLectureIds.contains(lecture.getId())
                ))
                .toList();
    }

    public LectureDetailResponse getDetail(Long id, String email) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));

        long enrolledCount = enrollmentRepository.countByLectureId(id);
        boolean enrolled = false;
        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            enrolled = enrollmentRepository.findByLectureIdAndUserId(id, user.getId()).isPresent();
        }

        return LectureDetailResponse.from(lecture, enrolledCount, enrolled);
    }

    /**
     * email(로그인한 사용자)이 주어진 lectureIds 중 신청한 강의 id 집합을 반환합니다.
     * 비로그인(email == null)인 경우 빈 집합을 반환합니다.
     */
    private Set<Long> resolveEnrolledLectureIds(String email, List<Long> lectureIds) {
        if (email == null) {
            return Collections.emptySet();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return enrollmentRepository.findByUserIdAndLectureIdIn(user.getId(), lectureIds).stream()
                .map(enrollment -> enrollment.getLecture().getId())
                .collect(Collectors.toSet());
    }

    /**
     * 게시글 상태 변경. 컨트롤러에서 ADMIN 권한 검증(SecurityConfig)을 마친 뒤 호출됩니다.
     */
    @Transactional
    public LectureDetailResponse updateStatus(Long id, LectureStatus status) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        lecture.changeStatus(status);
        long enrolledCount = enrollmentRepository.countByLectureId(id);
        return LectureDetailResponse.from(lecture, enrolledCount, false);
    }

    @Transactional
    public void delete(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        enrollmentRepository.deleteByLectureId(id);
        lectureRepository.delete(lecture);
    }
}
