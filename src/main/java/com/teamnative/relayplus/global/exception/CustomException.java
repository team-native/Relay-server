package com.teamnative.relayplus.global.exception;

/**
 * 비즈니스 로직에서 의도적으로 발생시키는 예외입니다.
 * Service 단에서 throw new CustomException(ErrorCode.XXX) 형태로 사용합니다.
 */
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
