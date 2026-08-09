package com.teamnative.relayplus.domain.lecture.entity;

import com.teamnative.relayplus.domain.auth.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 릴레이 스터디 상세페이지의 댓글입니다.
 * 표시 항목: 작성자, 작성자의 기수/학과, 댓글 본문, 작성 시간(상대 시간)
 */
@Entity
@Table(name = "lecture_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false, updatable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private User author;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public LectureComment(Lecture lecture, User author, String content) {
        this.lecture = lecture;
        this.author = author;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
