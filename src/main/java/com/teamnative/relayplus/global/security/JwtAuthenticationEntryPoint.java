package com.teamnative.relayplus.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnative.relayplus.global.exception.ErrorCode;
import com.teamnative.relayplus.global.jwt.JwtAuthenticationFilter;
import com.teamnative.relayplus.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 요청(토큰 없음/유효하지 않음/만료됨)이 보호된 엔드포인트에 접근했을 때 호출됩니다.
 * GlobalExceptionHandler는 컨트롤러 이후 단계에서만 동작하므로,
 * 필터 단계에서 걸러지는 인증 실패는 이 EntryPoint가 없으면 Spring Security 기본 응답(포맷 불일치)이 나갑니다.
 *
 * JwtAuthenticationFilter가 request attribute에 남긴 토큰 상태를 읽어,
 * 만료된 토큰이면 EXPIRED_TOKEN(클라이언트가 /reissue를 호출하면 되는 경우)을,
 * 그 외(토큰 없음/위조/형식 오류)에는 INVALID_TOKEN(재로그인이 필요한 경우)을 응답합니다.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        Object status = request.getAttribute(JwtAuthenticationFilter.TOKEN_STATUS_ATTRIBUTE);
        ErrorCode errorCode = status == JwtTokenProvider.TokenStatus.EXPIRED
                ? ErrorCode.EXPIRED_TOKEN
                : ErrorCode.INVALID_TOKEN;

        SecurityErrorResponseWriter.write(response, objectMapper, errorCode);
    }
}
