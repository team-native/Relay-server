package com.teamnative.relayplus.domain.enrollment.entity;

import com.teamnative.relayplus.domain.auth.entity.User;
import com.teamnative.relayplus.domain.lecture.entity.Lecture;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 강의 신청 엔티티입니다.
 * 사용자가 강의(lecture)에 신청하면 자동으로 신청 완료됩니다.
 *
 * 특징:
 * - 한 사용자는 같은 강의에 중복으로 신청할 수 없습니다 (unique constraint: lecture_id + user_id)
 * - 신청하면 자동으로 신청 완료 상태입니다.
 * - 신청 취소는 enrollmentRepository에서 delete로 처리합니다.
 */
@Entity
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_lecture_user",
                columnNames = {"lecture_id", "user_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", nullable = false, updatable = false)
    private Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Enrollment(Lecture lecture, User user) {
        this.lecture = lecture;
        this.user = user;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
