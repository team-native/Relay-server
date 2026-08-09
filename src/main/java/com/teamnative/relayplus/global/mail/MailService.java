package com.teamnative.relayplus.global.mail;

import java.time.Duration;

public interface MailService {

    void sendVerificationCode(String to, String code, Duration ttl);
}
