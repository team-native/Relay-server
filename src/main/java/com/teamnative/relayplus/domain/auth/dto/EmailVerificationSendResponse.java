package com.teamnative.relayplus.domain.auth.dto;

public record EmailVerificationSendResponse(
        String email,
        long expiresInSeconds,
        long resendCooldownSeconds
) {
    public static EmailVerificationSendResponse of(
            String email,
            long expiresInSeconds,
            long resendCooldownSeconds
    ) {
        return new EmailVerificationSendResponse(email, expiresInSeconds, resendCooldownSeconds);
    }
}
