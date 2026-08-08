package com.teamnative.relayplus.domain.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationCleanupScheduler.class);

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationCleanupScheduler(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void purgeStaleVerifications() {
        try {
            emailVerificationService.purgeStale();
        } catch (Exception e) {
            log.warn("Failed to purge stale email verifications", e);
        }
    }
}
