package com.teamnative.relayplus.domain.lecture.repository;

import com.teamnative.relayplus.domain.lecture.entity.LectureComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureCommentRepository extends JpaRepository<LectureComment, Long> {

    // 특정 강의(게시글)의 댓글을 최신순으로 조회
    List<LectureComment> findByLecture_IdOrderByCreatedAtDesc(Long lectureId);
}
