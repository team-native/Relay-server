package com.teamnative.relayplus.global.mail;

import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
public class SmtpMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpMailService.class);

    private static final String SUBJECT = "[Relay+] 회원가입 이메일 인증번호입니다";

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpMailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void sendVerificationCode(String to, String code, Duration ttl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(SUBJECT);
            helper.setText(buildBody(code, ttl), true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            log.error("Failed to send verification mail to {}", to, e);
            throw new CustomException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    private String buildBody(String code, Duration ttl) {
        return """
                <div style="max-width:480px;margin:0 auto;padding:32px 24px;font-family:'Apple SD Gothic Neo',sans-serif;">
                  <h1 style="font-size:24px;font-weight:800;color:#000;margin:0 0 12px;">이메일 인증</h1>
                  <p style="font-size:15px;color:#666;margin:0 0 28px;line-height:1.6;">
                    아래 인증번호를 회원가입 화면에 입력해주세요.
                  </p>
                  <div style="background:#f8f9fa;border-radius:16px;padding:24px;text-align:center;">
                    <span style="font-size:32px;font-weight:800;letter-spacing:8px;color:#000;">%s</span>
                  </div>
                  <p style="font-size:13px;color:#999;margin:24px 0 0;line-height:1.6;">
                    인증번호는 %s 동안 유효합니다.<br>
                    본인이 요청하지 않았다면 이 메일은 무시해주세요.
                  </p>
                </div>
                """.formatted(code, formatTtl(ttl));
    }

    private String formatTtl(Duration ttl) {
        long seconds = ttl.toSeconds();
        return seconds % 60 == 0
                ? ttl.toMinutes() + "분"
                : seconds + "초";
    }
}
