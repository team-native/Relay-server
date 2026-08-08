package com.teamnative.relayplus.domain.auth.repository;

import com.teamnative.relayplus.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * email이 PK이므로 조회/삭제는 JpaRepository의 findById / deleteById를 그대로 사용합니다.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
