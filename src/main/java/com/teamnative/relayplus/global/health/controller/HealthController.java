package com.teamnative.relayplus.global.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 배포/모니터링용 헬스체크 엔드포인트입니다.
 * - Docker(HEALTHCHECK), 로드밸런서, 배포 스크립트 등이 서버 생존 여부를
 *   확인하는 용도이므로 인증 없이 접근 가능해야 합니다. (SecurityConfig에서 permitAll)
 * - GET /health : 서버가 요청을 받을 수 있으면 200 OK를 반환합니다.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
