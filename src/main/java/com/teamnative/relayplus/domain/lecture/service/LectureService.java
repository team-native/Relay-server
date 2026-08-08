package com.teamnative.relayplus.domain.lecture.service;

import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
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

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    public LectureService(LectureRepository lectureRepository, UserRepository userRepository) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
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
                .speaker(request.speaker())
                .lectureAt(request.lectureAt())
                .capacity(request.capacity())
                .description(request.description())
                .author(author)
                .build();

        Lecture saved = lectureRepository.save(lecture);
        return LectureDetailResponse.from(saved);
    }

    /**
     * 메인페이지 / 개설미정 / 개설확정 / 종료 목록 조회를 겸합니다.
     * status가 null이면 전체, keyword가 있으면 제목 검색까지 적용됩니다.
     */
    public List<LectureSummaryResponse> getList(LectureStatus status, String keyword) {
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
            throw new CustomException(resolveEmptyErrorCode(status, hasKeyword));
        }

        return lectures.stream()
                .map(LectureSummaryResponse::from)
                .toList();
    }

    public LectureDetailResponse getDetail(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        return LectureDetailResponse.from(lecture);
    }

    /**
     * 게시글 상태 변경. 컨트롤러에서 ADMIN 권한 검증(SecurityConfig)을 마친 뒤 호출됩니다.
     */
    @Transactional
    public LectureDetailResponse updateStatus(Long id, LectureStatus status) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        lecture.changeStatus(status);
        return LectureDetailResponse.from(lecture);
    }

    private ErrorCode resolveEmptyErrorCode(LectureStatus status, boolean hasKeyword) {
        // 검색어가 있었다면 상태 필터 여부와 무관하게 "검색 결과가 없습니다"가 우선합니다.
        if (hasKeyword) {
            return ErrorCode.LECTURE_SEARCH_RESULT_EMPTY;
        }
        if (status == null) {
            return ErrorCode.LECTURE_NOT_FOUND;
        }
        return switch (status) {
            case PENDING -> ErrorCode.PENDING_LECTURE_EMPTY;
            case CONFIRMED -> ErrorCode.CONFIRMED_LECTURE_EMPTY;
            case CLOSED -> ErrorCode.CLOSED_LECTURE_EMPTY;
        };
    }
}
