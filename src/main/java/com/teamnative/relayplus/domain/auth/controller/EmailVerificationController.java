package com.teamnative.relayplus.domain.auth.controller;

import com.teamnative.relayplus.domain.auth.dto.EmailVerificationConfirmRequest;
import com.teamnative.relayplus.domain.auth.dto.EmailVerificationSendRequest;
import com.teamnative.relayplus.domain.auth.dto.EmailVerificationSendResponse;
import com.teamnative.relayplus.domain.auth.service.EmailVerificationService;
import com.teamnative.relayplus.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/email")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<EmailVerificationSendResponse>> send(
            @Valid @RequestBody EmailVerificationSendRequest request
    ) {
        EmailVerificationSendResponse response = emailVerificationService.send(request.email());
        return ResponseEntity
                .ok(ApiResponse.success("인증번호를 발송했습니다. 메일함을 확인해주세요.", response));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @Valid @RequestBody EmailVerificationConfirmRequest request
    ) {
        emailVerificationService.verify(request.email(), request.code());
        return ResponseEntity
                .ok(ApiResponse.success("이메일 인증이 완료되었습니다.", null));
    }
}
