package com.teamnative.relayplus.domain.lecture.repository;

import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // 메인페이지: 전체 게시글 최신순
    List<Lecture> findAllByOrderByCreatedAtDesc();

    // 상태별 필터 (개설미정 / 개설확정 / 종료) 최신순
    List<Lecture> findByStatusOrderByCreatedAtDesc(LectureStatus status);

    // 제목 검색 (상태 무관) 최신순
    List<Lecture> findByTitleContainingOrderByCreatedAtDesc(String keyword);

    // 상태별 + 제목 검색 최신순
    List<Lecture> findByStatusAndTitleContainingOrderByCreatedAtDesc(LectureStatus status, String keyword);
}
