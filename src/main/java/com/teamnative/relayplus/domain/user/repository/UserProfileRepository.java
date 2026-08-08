package com.teamnative.relayplus.domain.user.repository;

import com.teamnative.relayplus.domain.user.entity.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * user 도메인(프로필)의 리포지토리입니다.
 * auth 도메인에도 같은 테이블(users)을 매핑하는 UserRepository가 있어 이름이 겹치면
 * Spring Data JPA가 두 리포지토리를 같은 빈 이름(userRepository)으로 등록하려다
 * 충돌합니다(BeanDefinitionOverrideException). 그래서 이름을 다르게 둡니다.
 */
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
}
