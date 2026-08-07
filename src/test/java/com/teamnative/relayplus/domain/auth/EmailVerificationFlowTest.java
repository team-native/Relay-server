package com.teamnative.relayplus.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnative.relayplus.domain.auth.entity.EmailVerification;
import com.teamnative.relayplus.domain.auth.repository.EmailVerificationRepository;
import com.teamnative.relayplus.domain.auth.repository.UserRepository;
import com.teamnative.relayplus.global.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmailVerificationFlowTest {

    private static final String EMAIL = "s24001@gsm.hs.kr";
    private static final String PASSWORD = "relay1234";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private MailService mailService;

    @BeforeEach
    void clear() {
        emailVerificationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("인증번호를 발송하면 6자리 코드가 메일로 나가고, 원문이 아닌 해시가 저장된다")
    void sendIssuesHashedCode() throws Exception {
        String code = sendCodeAndCapture();

        assertThat(code).matches("\\d{6}");

        EmailVerification saved = emailVerificationRepository.findById(EMAIL).orElseThrow();
        assertThat(saved.getCodeHash()).isNotEqualTo(code);
        assertThat(saved.isVerified()).isFalse();
        assertThat(saved.getAttemptCount()).isZero();
    }

    @Test
    @DisplayName("쿨다운이 지나기 전에 재발송을 요청하면 429로 거절한다")
    void resendWithinCooldownIsRejected() throws Exception {
        sendCodeAndCapture();

        mockMvc.perform(post("/api/auth/email/send")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", EMAIL))))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("인증번호가 틀리면 400이고, 실패 횟수가 롤백되지 않고 남는다")
    void wrongCodeIncreasesAttemptCount() throws Exception {
        String code = sendCodeAndCapture();
        String wrongCode = code.equals("000000") ? "111111" : "000000";

        mockMvc.perform(post("/api/auth/email/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", EMAIL, "code", wrongCode))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("인증번호가 일치하지 않습니다."));

        EmailVerification saved = emailVerificationRepository.findById(EMAIL).orElseThrow();
        assertThat(saved.getAttemptCount()).isEqualTo(1);
        assertThat(saved.isVerified()).isFalse();
    }

    @Test
    @DisplayName("인증을 마치지 않은 이메일로는 회원가입할 수 없다")
    void signupWithoutVerificationIsRejected() throws Exception {
        sendCodeAndCapture();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(signupBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("학교 이메일 인증을 먼저 완료해주세요."));

        assertThat(userRepository.existsByEmail(EMAIL)).isFalse();
    }

    @Test
    @DisplayName("인증 후 회원가입하면 가입에 성공하고 인증 이력은 소진된다")
    void verifiedEmailCanSignupOnce() throws Exception {
        String code = sendCodeAndCapture();

        mockMvc.perform(post("/api/auth/email/verify")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", EMAIL, "code", code))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertThat(emailVerificationRepository.findById(EMAIL).orElseThrow().isVerified()).isTrue();

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(signupBody()))
                .andExpect(status().isCreated());

        assertThat(userRepository.existsByEmail(EMAIL)).isTrue();
        assertThat(emailVerificationRepository.findById(EMAIL)).isEmpty();
    }

    @Test
    @DisplayName("이미 가입된 이메일로는 인증번호를 보내지 않는다")
    void sendToAlreadyRegisteredEmailIsRejected() throws Exception {
        verifiedEmailCanSignupOnce();

        mockMvc.perform(post("/api/auth/email/send")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", EMAIL))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일 입니다."));
    }

    @Test
    @DisplayName("학교 이메일 형식이 아니면 발송 요청 자체가 거절된다")
    void nonSchoolEmailIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/email/send")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", "someone@gmail.com"))))
                .andExpect(status().isBadRequest());
    }

    private String sendCodeAndCapture() throws Exception {
        mockMvc.perform(post("/api/auth/email/send")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", EMAIL))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.expiresInSeconds").value(180))
                .andExpect(jsonPath("$.data.resendCooldownSeconds").value(60));

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendVerificationCode(eq(EMAIL), codeCaptor.capture(), any(Duration.class));
        return codeCaptor.getValue();
    }

    private String signupBody() throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "name", "홍길동",
                "email", EMAIL,
                "password", PASSWORD,
                "passwordConfirm", PASSWORD,
                "generation", 10,
                "department", "SW_DEVELOPMENT"
        ));
    }
}
