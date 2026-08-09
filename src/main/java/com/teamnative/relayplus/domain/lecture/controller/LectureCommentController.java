package com.teamnative.relayplus.domain.lecture.controller;

import com.teamnative.relayplus.domain.lecture.dto.LectureCommentCreateRequest;
import com.teamnative.relayplus.domain.lecture.dto.LectureCommentResponse;
import com.teamnative.relayplus.domain.lecture.service.LectureCommentService;
import com.teamnative.relayplus.global.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 강의 상세페이지 댓글 API
 * - GET  /api/lectures/lecture/{id}/comments  : 댓글 목록 (등록 오래된순)
 * - POST /api/lectures/lecture/{id}/comments  : 댓글 작성
 */
@RestController
@RequestMapping("/api/lectures/lecture/{id}/comments")
public class LectureCommentController {

    private final LectureCommentService lectureCommentService;

    public LectureCommentController(LectureCommentService lectureCommentService) {
        this.lectureCommentService = lectureCommentService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LectureCommentResponse>>> getList(@PathVariable Long id) {
        List<LectureCommentResponse> response = lectureCommentService.getList(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LectureCommentResponse>> create(
            @PathVariable Long id,
            @Valid @RequestBody LectureCommentCreateRequest request,
            Authentication authentication
    ) {
        LectureCommentResponse response = lectureCommentService.create(id, request, authentication.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 등록되었습니다.", response));
    }
}
