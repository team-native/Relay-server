package com.teamnative.relayplus.domain.auth.entity;

/**
 * Spring Security 권한입니다. 현재는 USER만 사용하지만
 * 추후 운영자 기능(공지사항 관리 등)을 위해 ADMIN을 미리 정의해둡니다.
 */
public enum Role {
    USER,
    ADMIN
}
