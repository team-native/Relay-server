package com.teamnative.relayplus.domain.auth.service;

import com.teamnative.relayplus.domain.auth.dto.EmailVerificationSendResponse;
import com.teamnative.relayplus.domain.auth.entity.EmailVerification;
import com.teamnative.relayplus.domain.auth.repository.EmailVerificationRepository;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import com.teamnative.relayplus.global.mail.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
@Transactional(readOnly = true)
public class EmailVerificationService {

    private static final int CODE_BOUND = 1_000_000;
    private static final String CODE_FORMAT = "%06d";

    private final EmailVerificationRepository emailVerificationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SecureRandom random = new SecureRandom();

    private final Duration codeTtl;
    private final Duration resendCooldown;
    private final Duration verifiedValidity;
    private final int maxAttempts;

    public EmailVerificationService(
            EmailVerificationRepository emailVerificationRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            MailService mailService,
            @Value("${auth.email-verification.code-ttl-seconds:180}") long codeTtlSeconds,
            @Value("${auth.email-verification.resend-cooldown-seconds:60}") long resendCooldownSeconds,
            @Value("${auth.email-verification.verified-validity-minutes:30}") long verifiedValidityMinutes,
            @Value("${auth.email-verification.max-attempts:5}") int maxAttempts
    ) {
        this.emailVerificationRepository = emailVerificationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.codeTtl = Duration.ofSeconds(codeTtlSeconds);
        this.resendCooldown = Duration.ofSeconds(resendCooldownSeconds);
        this.verifiedValidity = Duration.ofMinutes(verifiedValidityMinutes);
        this.maxAttempts = maxAttempts;
    }

    @Transactional
    public EmailVerificationSendResponse send(String email) {

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        LocalDateTime now = LocalDateTime.now();
        EmailVerification existing = emailVerificationRepository.findById(email).orElse(null);

        if (existing != null && now.isBefore(existing.getLastSentAt().plus(resendCooldown))) {
            throw new CustomException(ErrorCode.VERIFICATION_RESEND_TOO_SOON);
        }

        String code = generateCode();
        String codeHash = passwordEncoder.encode(code);
        LocalDateTime expiresAt = now.plus(codeTtl);

        if (existing != null) {
            existing.renew(codeHash, now, expiresAt);
        } else {
            emailVerificationRepository.save(new EmailVerification(email, codeHash, now, expiresAt));
        }

        mailService.sendVerificationCode(email, code, codeTtl);

        return EmailVerificationSendResponse.of(email, codeTtl.toSeconds(), resendCooldown.toSeconds());
    }

    @Transactional(noRollbackFor = CustomException.class)
    public void verify(String email, String code) {

        EmailVerification verification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        if (verification.isCodeExpired(now)) {
            throw new CustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }

        if (verification.getAttemptCount() >= maxAttempts) {
            throw new CustomException(ErrorCode.VERIFICATION_ATTEMPT_EXCEEDED);
        }

        if (!passwordEncoder.matches(code, verification.getCodeHash())) {
            verification.increaseAttemptCount();
            throw new CustomException(ErrorCode.VERIFICATION_CODE_MISMATCH);
        }

        verification.markVerified(now);
    }

    public void assertVerified(String email) {
        EmailVerification verification = emailVerificationRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_VERIFIED));

        if (!verification.isVerified()) {
            throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (verification.isVerificationExpired(LocalDateTime.now(), verifiedValidity)) {
            throw new CustomException(ErrorCode.EMAIL_VERIFICATION_EXPIRED);
        }
    }

    public void consume(String email) {
        emailVerificationRepository.deleteById(email);
    }


    @Transactional
    public void purgeStale() {
        LocalDateTime now = LocalDateTime.now();
        emailVerificationRepository.deleteByVerifiedAtIsNullAndExpiresAtBefore(now);
        emailVerificationRepository.deleteByVerifiedAtBefore(now.minus(verifiedValidity));
    }

    private String generateCode() {
        return CODE_FORMAT.formatted(random.nextInt(CODE_BOUND));
    }
}
