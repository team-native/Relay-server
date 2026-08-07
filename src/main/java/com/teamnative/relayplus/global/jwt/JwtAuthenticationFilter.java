package com.teamnative.relayplus.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * 요청 헤더의 Authorization: Bearer {token}을 확인하여
 * 유효한 토큰이면 SecurityContext에 인증 정보를 등록합니다.
 * 로그인/회원가입 등 permitAll 대상 경로는 SecurityConfig에서 필터를 우회하도록 설정합니다.
 * 토큰의 role 클레임을 읽어 "ROLE_" 접두사를 붙인 권한을 부여합니다.
 *
 * 인증에 실패한 경우, 만료된 토큰인지 그 외 사유(위조/형식 오류/토큰 없음)인지를
 * request attribute({@link #TOKEN_STATUS_ATTRIBUTE})에 남겨서 JwtAuthenticationEntryPoint가
 * 서로 다른 에러 메시지(EXPIRED_TOKEN / INVALID_TOKEN)로 응답할 수 있게 합니다.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_STATUS_ATTRIBUTE = "jwtTokenStatus";

    private static final String HEADER = "Authorization";
    private static final String PREFIX = "Bearer ";
    private static final String DEFAULT_ROLE = "USER";
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            JwtTokenProvider.TokenStatus status = jwtTokenProvider.validate(token);

            if (status == JwtTokenProvider.TokenStatus.VALID && !jwtTokenProvider.isRefreshToken(token)) {
                authenticate(request, token);
            } else {
                // Refresh Token이 이 자리에 잘못 쓰인 경우도 만료가 아닌 "유효하지 않음"으로 취급합니다.
                boolean expired = status == JwtTokenProvider.TokenStatus.EXPIRED;
                request.setAttribute(
                        TOKEN_STATUS_ATTRIBUTE,
                        expired ? JwtTokenProvider.TokenStatus.EXPIRED : JwtTokenProvider.TokenStatus.INVALID
                );
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String token) {
        String email = jwtTokenProvider.getEmail(token);

        // role 클레임이 없는 토큰(예: 이 변경 이전에 발급된 토큰)은 USER로 취급합니다.
        String role = jwtTokenProvider.getRole(token);
        String authority = ROLE_PREFIX + (role != null ? role : DEFAULT_ROLE);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority(authority))
                );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if (header != null && header.startsWith(PREFIX)) {
            return header.substring(PREFIX.length());
        }
        return null;
    }
}
