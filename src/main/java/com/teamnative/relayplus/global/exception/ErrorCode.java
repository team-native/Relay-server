package com.teamnative.relayplus.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 프로젝트 전역에서 사용하는 에러 코드입니다.
 * 기능명세서에 정의된 에러 메시지를 그대로 매핑합니다.
 * 다른 도메인(post, notice 등) 작업 시 이 enum에 항목을 추가해서 사용하면 됩니다.
 */
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),

    // Auth / User (기능명세서 - 회원가입/로그인)
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일 입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 영문과 숫자를 포함하여 8자 이상 64자 이하여야 합니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "만료되었거나 존재하지 않는 Refresh Token입니다. 다시 로그인해주세요."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
