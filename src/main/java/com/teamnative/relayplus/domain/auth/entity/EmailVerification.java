package com.teamnative.relayplus.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

    @Id
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "code_hash", nullable = false)
    private String codeHash;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime lastSentAt;

    @Column(nullable = false)
    private int attemptCount;

    private LocalDateTime verifiedAt;

    public EmailVerification(String email, String codeHash, LocalDateTime sentAt, LocalDateTime expiresAt) {
        this.email = email;
        this.codeHash = codeHash;
        this.lastSentAt = sentAt;
        this.expiresAt = expiresAt;
        this.attemptCount = 0;
    }

    public void renew(String codeHash, LocalDateTime sentAt, LocalDateTime expiresAt) {
        this.codeHash = codeHash;
        this.lastSentAt = sentAt;
        this.expiresAt = expiresAt;
        this.attemptCount = 0;
        this.verifiedAt = null;
    }

    public void increaseAttemptCount() {
        this.attemptCount++;
    }

    public void markVerified(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public boolean isCodeExpired(LocalDateTime now) {
        return now.isAfter(expiresAt);
    }

    public boolean isVerificationExpired(LocalDateTime now, Duration validity) {
        return verifiedAt == null || now.isAfter(verifiedAt.plus(validity));
    }
}
