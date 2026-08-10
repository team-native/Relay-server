package com.teamnative.relayplus.domain.notice.controller;

import com.teamnative.relayplus.domain.notice.dto.NoticeCreateRequest;
import com.teamnative.relayplus.domain.notice.dto.NoticeListResponse;
import com.teamnative.relayplus.domain.notice.dto.NoticeResponse;
import com.teamnative.relayplus.domain.notice.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항 API
 * - GET /api/notice           : 공지사항 목록 (페이징)
 * - GET /api/notice/:noticeId : 공지사항 상세
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /**
     * 공지사항 목록 조회
     * 최신순으로 정렬되며, 페이징을 지원합니다.
     *
     * @param pageable 페이징 정보 (기본값: 첫 페이지, 10개씩)
     */
    @GetMapping
    public ResponseEntity<NoticeListResponse> getNotices(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        NoticeListResponse response = noticeService.getNotices(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponse> getNotice(
            @PathVariable Long noticeId
    ) {
        NoticeResponse response = noticeService.getNotice(noticeId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/new")
    public ResponseEntity<NoticeResponse> createNotice(
            @Valid @RequestBody NoticeCreateRequest request
    ) {
        NoticeResponse response = noticeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
