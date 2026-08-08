package com.teamnative.relayplus.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 토큰 발급 및 검증을 담당합니다.
 * application.yml의 jwt.secret / jwt.expiration / jwt.refresh-expiration 값을 사용합니다.
 * Access Token과 Refresh Token은 payload의 "category" 클레임으로 구분합니다.
 * (같은 secret으로 서명되므로, Refresh Token이 Access Token 자리에 잘못 쓰이는 것을 막기 위함)
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_CATEGORY = "category";
    private static final String CLAIM_ROLE = "role";
    private static final String CATEGORY_ACCESS = "access";
    private static final String CATEGORY_REFRESH = "refresh";

    private final SecretKey secretKey;
    private final long accessExpirationMillis;
    private final long refreshExpirationMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration:3600000}") long accessExpirationMillis,
            @Value("${jwt.refresh-expiration:1209600000}") long refreshExpirationMillis
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessExpirationMillis = accessExpirationMillis;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    /**
     * 이메일을 subject로, role을 클레임으로 담은 Access Token을 발급합니다.
     * role은 "USER", "ADMIN"처럼 접두사(ROLE_) 없는 원시 값으로 넘기세요. 인가 시 ROLE_ 접두사는
     * JwtAuthenticationFilter에서 붙입니다.
     */
    public String generateToken(String email, String role) {
        return generateToken(email, CATEGORY_ACCESS, accessExpirationMillis, role);
    }

    /**
     * 이메일을 subject로 하는 Refresh Token을 발급합니다.
     * Refresh Token에는 role을 담지 않습니다. 재발급 시 DB에서 최신 role을 다시 조회해서
     * 새 Access Token에 반영하므로, role이 변경된 뒤에도 재로그인 없이 최신 권한이 적용됩니다.
     */
    public String generateRefreshToken(String email) {
        return generateToken(email, CATEGORY_REFRESH, refreshExpirationMillis, null);
    }

    private String generateToken(String email, String category, long expirationMillis, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMillis);

        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim(CLAIM_CATEGORY, category)
                .issuedAt(now)
                .expiration(expiry);

        if (role != null) {
            builder.claim(CLAIM_ROLE, role);
        }

        return builder.signWith(secretKey).compact();
    }

    /**
     * 토큰에서 이메일(subject)을 추출합니다.
     * 반드시 validateToken()으로 먼저 유효성을 확인한 뒤 호출하세요.
     */
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 토큰에서 role 클레임을 추출합니다. Refresh Token이거나 role이 없는 토큰이면 null을 반환합니다.
     */
    public String getRole(String token) {
        return parseClaims(token).get(CLAIM_ROLE, String.class);
    }

    /**
     * Refresh Token 여부를 확인합니다. (category 클레임 검사)
     */
    public boolean isRefreshToken(String token) {
        String category = parseClaims(token).get(CLAIM_CATEGORY, String.class);
        return CATEGORY_REFRESH.equals(category);
    }

    /**
     * 토큰 유효성을 검증합니다. 만료/변조된 토큰이면 false를 반환합니다.
     * 만료와 변조를 구분해야 하는 경우(예: /reissue)에는 validate(String)을 사용하세요.
     */
    public boolean validateToken(String token) {
        return validate(token) == TokenStatus.VALID;
    }

    /**
     * 토큰 상태를 세분화해서 반환합니다.
     * - EXPIRED: 서명은 유효하지만 만료된 토큰 (재로그인 유도 등 별도 처리가 필요할 수 있음)
     * - INVALID: 서명 위조, 형식 오류 등 그 외 모든 검증 실패
     */
    public TokenStatus validate(String token) {
        try {
            parseClaims(token);
            return TokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return TokenStatus.EXPIRED;
        } catch (JwtException | IllegalArgumentException e) {
            return TokenStatus.INVALID;
        }
    }

    public enum TokenStatus {
        VALID, EXPIRED, INVALID
    }

    public long getRefreshExpirationMillis() {
        return refreshExpirationMillis;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
