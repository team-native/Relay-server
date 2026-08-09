package com.teamnative.relayplus.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnative.relayplus.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증은 됐지만 권한이 없는 요청(예: USER가 ADMIN 전용 API 호출)에 대한 403 응답을
 * ApiResponse 포맷으로 통일합니다.
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        SecurityErrorResponseWriter.write(response, objectMapper, ErrorCode.ACCESS_DENIED);
    }
}
