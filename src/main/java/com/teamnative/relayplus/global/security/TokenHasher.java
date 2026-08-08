package com.teamnative.relayplus.global.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Refresh Token을 DB에 저장하기 전에 해시하기 위한 유틸리티입니다.
 * Refresh Token 원문을 그대로 저장하면 DB 유출 시 공격자가 바로 유효한 토큰을 얻게 되므로,
 * SHA-256 해시값만 저장하고 조회 시에도 들어온 토큰을 같은 방식으로 해시해 비교합니다.
 *
 * BCrypt 대신 SHA-256을 쓰는 이유: BCrypt는 72바이트를 초과하는 입력을 자르는데,
 * JWT는 보통 그보다 훨씬 길어서 뒷부분이 잘려나갑니다. Refresh Token은 이미 무작위성이
 * 충분히 큰 값(서명 포함)이라 별도의 salt 없이 결정적 해시(SHA-256)로 비교해도 안전합니다.
 */
public final class TokenHasher {

    private TokenHasher() {
    }

    public static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // 모든 표준 JVM에 SHA-256은 반드시 존재하므로 실질적으로 발생하지 않습니다.
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
