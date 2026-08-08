package com.teamnative.relayplus.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 회원 엔티티입니다.
 * 회원가입 명세: 이름, 학교 이메일, 비밀번호, 기수, 학과
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    // BCrypt로 암호화된 값이 저장됩니다. 평문 저장 금지.
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Generation generation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public User(String name, String email, String password, Generation generation, Department department) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.generation = generation;
        this.department = department;
        this.role = Role.USER;
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
     * 비밀번호 변경(마이페이지 등)에서 사용할 메서드입니다.
     * 반드시 암호화된 값을 넘겨야 합니다.
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}
