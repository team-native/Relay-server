package com.teamnative.relayplus.domain.auth.repository;

import com.teamnative.relayplus.domain.auth.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, String> {

    void deleteByVerifiedAtIsNullAndExpiresAtBefore(LocalDateTime threshold);
    void deleteByVerifiedAtBefore(LocalDateTime threshold);
}
