package com.teamnative.relayplus.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 모든 API 응답을 감싸는 공통 응답 포맷입니다.
 * success: 요청 성공 여부
 * message: 사용자에게 보여줄 메시지 (성공/실패 모두)
 * data: 실제 응답 데이터 (없을 경우 응답 body에서 생략됨)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
