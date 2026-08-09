package com.teamnative.relayplus.domain.lecture.service;

import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.domain.lecture.dto.LectureCommentCreateRequest;
import com.teamnative.relayplus.domain.lecture.dto.LectureCommentResponse;
import com.teamnative.relayplus.domain.lecture.entity.LectureComment;
import com.teamnative.relayplus.domain.lecture.repository.LectureCommentRepository;
import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.repository.LectureRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LectureCommentService {

    private final LectureCommentRepository lectureCommentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    public LectureCommentService(LectureCommentRepository lectureCommentRepository,
                                  LectureRepository lectureRepository,
                                  UserRepository userRepository) {
        this.lectureCommentRepository = lectureCommentRepository;
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LectureCommentResponse create(Long lectureId, LectureCommentCreateRequest request, String email) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomException(ErrorCode.LECTURE_NOT_FOUND));
        User author = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LectureComment comment = LectureComment.builder()
                .lecture(lecture)
                .author(author)
                .content(request.content())
                .build();

        LectureComment saved = lectureCommentRepository.save(comment);
        return LectureCommentResponse.from(saved);
    }

    public List<LectureCommentResponse> getList(Long lectureId) {
        // 강의 자체가 없는데 댓글을 조회하려는 경우도 걸러줍니다.
        if (!lectureRepository.existsById(lectureId)) {
            throw new CustomException(ErrorCode.LECTURE_NOT_FOUND);
        }

        return lectureCommentRepository.findByLecture_IdOrderByCreatedAtDesc(lectureId).stream()
                .map(LectureCommentResponse::from)
                .toList();
    }
}
