package com.teamnative.relayplus.global.exception;

import com.teamnative.relayplus.global.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기입니다.
 * CustomException과 @Valid 검증 실패(MethodArgumentNotValidException)를
 * 공통 응답 포맷(ApiResponse)으로 변환합니다.
 *
 * [수정사항]
 * - JWT 예외(JwtException, ExpiredJwtException, MalformedJwtException, SignatureException) 처리 추가
 * - 토큰 관련 모든 예외를 401 Unauthorized로 처리하여 500 에러 방지
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode.getMessage()));
    }

    /**
     * [추가] JWT 토큰이 만료된 경우 처리
     * 클라이언트는 이를 감지하여 /reissue 엔드포인트를 호출할 수 있습니다.
     */
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleExpiredJwtException(ExpiredJwtException e) {
        log.warn("Expired JWT token: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.EXPIRED_TOKEN.getStatus())
                .body(ApiResponse.fail(ErrorCode.EXPIRED_TOKEN.getMessage()));
    }

    /**
     * [추가] JWT 토큰의 서명이 위조된 경우 처리
     */
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<Void>> handleSignatureException(SignatureException e) {
        log.warn("Invalid JWT signature: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_TOKEN.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_TOKEN.getMessage()));
    }

    /**
     * [추가] JWT 토큰의 형식이 잘못된 경우 처리
     */
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJwtException(MalformedJwtException e) {
        log.warn("Malformed JWT token: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_TOKEN.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_TOKEN.getMessage()));
    }

    /**
     * [추가] 기타 모든 JWT 관련 예외 처리
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException e) {
        log.warn("JWT exception occurred: {}", e.getMessage());
        return ResponseEntity
                .status(ErrorCode.INVALID_TOKEN.getStatus())
                .body(ApiResponse.fail(ErrorCode.INVALID_TOKEN.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.INVALID_INPUT.getMessage());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ApiResponse.fail(message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("Unhandled exception occurred", e);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
