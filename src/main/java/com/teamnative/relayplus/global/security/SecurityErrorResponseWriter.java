package com.teamnative.relayplus.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnative.relayplus.global.exception.ErrorCode;
import com.teamnative.relayplus.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * 인증/인가 실패 응답을 ApiResponse 포맷으로 작성하는 공용 유틸리티입니다.
 * (JwtAuthenticationEntryPoint, JwtAccessDeniedHandler에서 공용으로 사용)
 */
final class SecurityErrorResponseWriter {

    private SecurityErrorResponseWriter() {
    }

    static void write(HttpServletResponse response, ObjectMapper objectMapper, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(errorCode.getMessage())));
    }
}
