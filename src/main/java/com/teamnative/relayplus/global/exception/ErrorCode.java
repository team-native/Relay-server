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
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증번호를 먼저 요청해주세요."),
    VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었습니다. 재발송 버튼을 눌러주세요."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
    VERIFICATION_ATTEMPT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "인증 시도 횟수를 초과했습니다. 인증번호를 다시 요청해주세요."),
    VERIFICATION_RESEND_TOO_SOON(HttpStatus.TOO_MANY_REQUESTS, "인증번호를 다시 요청하려면 잠시 기다려주세요."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "학교 이메일 인증을 먼저 완료해주세요."),
    EMAIL_VERIFICATION_EXPIRED(HttpStatus.BAD_REQUEST, "이메일 인증이 만료되었습니다. 다시 인증해주세요."),
    MAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 발송에 실패했습니다. 잠시 후 다시 시도해주세요."),

    // Lecture (기능명세서 - 메인페이지/개설미정/개설확정/종료/강의상세/릴레이 스터디 등록)
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글이 없습니다."),
    LECTURE_SEARCH_RESULT_EMPTY(HttpStatus.NOT_FOUND, "검색 결과가 없습니다."),
    PENDING_LECTURE_EMPTY(HttpStatus.NOT_FOUND, "개설 미정 게시글이 없습니다."),
    CONFIRMED_LECTURE_EMPTY(HttpStatus.NOT_FOUND, "개설 확정된 게시글이 없습니다."),
    CLOSED_LECTURE_EMPTY(HttpStatus.NOT_FOUND, "개설 종료된 게시글이 없습니다."),

    // Notice
    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다.");

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
