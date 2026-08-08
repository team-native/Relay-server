package com.teamnative.relayplus.domain.auth.dto;

/**
 * 토큰 발급/재발급 결과입니다. 오직 토큰 자체에 대한 정보만 담습니다.
 * - POST /reissue 응답으로 그대로 사용됩니다.
 * - POST /login 에서는 사용자 정보와 함께 {@link LoginResponse}에 담겨 내려갑니다.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static TokenResponse of(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, refreshToken, "Bearer");
    }
}
