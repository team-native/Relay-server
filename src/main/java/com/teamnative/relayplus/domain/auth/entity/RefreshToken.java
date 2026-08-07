package com.teamnative.relayplus.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자별 Refresh Token을 저장하는 엔티티입니다.
 * 조회/삭제가 전부 이메일 기준으로 일어나므로 email을 PK로 사용합니다.
 * (기존에는 userId를 PK로 두고 email에 별도 unique 제약을 걸었는데, 실제 접근 경로와
 *  일치하지 않는 구조였습니다. userId는 참조용으로만 남겨둡니다.)
 * 사용자당 하나의 Refresh Token만 유지하며, 재발급/로그인 시마다 값을 갱신합니다(Rotation).
 * DB에 저장된 값과 다른 토큰이 재발급 요청에 사용되면 탈취/재사용으로 간주하고 즉시 폐기합니다.
 *
 * token 컬럼에는 Refresh Token 원문이 아니라 {@link com.teamnative.relayplus.global.security.TokenHasher}로
 * 만든 SHA-256 해시값(64자 hex)을 저장합니다. DB가 유출되더라도 원문 토큰을 복원할 수 없도록 하기 위함입니다.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public RefreshToken(String email, Long userId, String tokenHash, LocalDateTime expiryDate) {
        this.email = email;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
    }

    public void update(String tokenHash, LocalDateTime expiryDate) {
        this.tokenHash = tokenHash;
        this.expiryDate = expiryDate;
    }
}
