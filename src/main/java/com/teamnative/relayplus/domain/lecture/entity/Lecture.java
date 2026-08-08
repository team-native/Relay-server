package com.teamnative.relayplus.domain.lecture.entity;

import com.teamnative.relayplus.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 릴레이 스터디 게시글 엔티티입니다.
 * 등록 명세: 연사 제목, 날짜, 시간, 연사자, 모집 인원, 연사 소개
 * 신청(enrollment) 관련 정보는 이 엔티티에 두지 않고, enrollment 도메인에서
 * lectureId를 참조하는 별도 엔티티로 관리합니다. (모집 인원 대비 신청 인원 카운트도 그쪽에서 계산)
 */
@Entity
@Table(name = "lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    // 연사 날짜 + 시간
    @Column(nullable = false)
    private LocalDateTime lectureAt;

    @Column(nullable = false)
    private Integer capacity;

    @Lob
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LectureStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Lecture(String title, LocalDateTime lectureAt, Integer capacity,
                   String description, User author) {
        this.title = title;
        this.lectureAt = lectureAt;
        this.capacity = capacity;
        this.description = description;
        this.author = author;
        this.status = LectureStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상태 변경 (ADMIN 전용). LectureService에서 권한 검증 후 호출합니다.
     */
    public void changeStatus(LectureStatus status) {
        this.status = status;
    }
}
