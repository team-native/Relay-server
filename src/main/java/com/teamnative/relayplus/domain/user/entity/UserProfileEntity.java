package com.teamnative.relayplus.domain.user.entity;

import com.teamnative.relayplus.domain.auth.entity.Department;
import com.teamnative.relayplus.domain.auth.entity.Generation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 프로필 정보 엔티티입니다.
 * 마이페이지에서 사용자의 기본 정보(name, generation, department)를 관리합니다.
 *
 * userId는 auth/users.user_id와 동일합니다 (1:1 관계).
 *
 * JPA 엔티티 이름의 기본값은 클래스 단순명("User")인데, auth 도메인에도 같은 이름의
 * 엔티티(com.teamnative.relayplus.domain.auth.entity.User)가 있어 이름이 겹치면
 * Hibernate 부트스트랩 시 DuplicateMappingException이 발생합니다. 그래서 엔티티 이름을
 * 명시적으로 다르게 지정합니다. (테이블명은 그대로 "users"를 공유합니다.)
 */
@Entity(name = "UserProfile")
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileEntity {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Generation generation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public UserProfileEntity(Long userId, String name, Generation generation, Department department) {
        this.userId = userId;
        this.name = name;
        this.generation = generation;
        this.department = department;
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
     * 프로필 정보 수정
     */
    public void updateProfile(String name, Generation generation, Department department) {
        this.name = name;
        this.generation = generation;
        this.department = department;
    }
}
