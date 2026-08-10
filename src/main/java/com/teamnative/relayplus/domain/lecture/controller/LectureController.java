package com.teamnative.relayplus.domain.lecture.controller;

import com.teamnative.relayplus.domain.lecture.dto.LectureCreateRequest;
import com.teamnative.relayplus.domain.lecture.dto.LectureDetailResponse;
import com.teamnative.relayplus.domain.lecture.dto.LectureStatusUpdateRequest;
import com.teamnative.relayplus.domain.lecture.dto.LectureSummaryResponse;
import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;
import com.teamnative.relayplus.domain.lecture.service.LectureService;
import com.teamnative.relayplus.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 릴레이 스터디(강의) 관련 API
 * API 명세서의 프론트 페이지 경로(/home, /new, /lecture/:id)에 맞춰 엔드포인트를 구성했습니다.
 * - GET   /api/lectures/home             : 목록 (메인페이지). status(PENDING/CONFIRMED/CLOSED), keyword 쿼리 파라미터로 필터/검색
 * - GET   /api/lectures/lecture/{id}     : 강의 상세
 * - POST  /api/lectures/new              : 릴레이 스터디 등록
 * - PATCH /api/lectures/lecture/{id}/status : 게시글 상태 변경 (ADMIN 전용, SecurityConfig에서 권한 제한)
 */
@RestController
@RequestMapping("/api/lectures")
public class LectureController {

    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping("/lecture")
    public ResponseEntity<ApiResponse<List<LectureSummaryResponse>>> getList(
            @RequestParam(required = false) LectureStatus status,
            @RequestParam(required = false) String keyword
    ) {
        List<LectureSummaryResponse> response = lectureService.getList(status, keyword);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/lecture/{id}")
    public ResponseEntity<ApiResponse<LectureDetailResponse>> getDetail(@PathVariable Long id) {
        LectureDetailResponse response = lectureService.getDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/new")
    public ResponseEntity<ApiResponse<LectureDetailResponse>> create(
            @Valid @RequestBody LectureCreateRequest request,
            Authentication authentication
    ) {
        LectureDetailResponse response = lectureService.create(request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("릴레이 스터디가 등록되었습니다.", response));
    }

    @PatchMapping("/lecture/{id}/status")
    public ResponseEntity<ApiResponse<LectureDetailResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody LectureStatusUpdateRequest request
    ) {
        LectureDetailResponse response = lectureService.updateStatus(id, request.status());
        return ResponseEntity.ok(ApiResponse.success("게시글 상태가 변경되었습니다.", response));
    }
}
