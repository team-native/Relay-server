package com.teamnative.relayplus.domain.notice.service;

import com.teamnative.relayplus.domain.notice.dto.NoticeCreateRequest;
import com.teamnative.relayplus.domain.notice.dto.NoticeListResponse;
import com.teamnative.relayplus.domain.notice.dto.NoticeResponse;
import com.teamnative.relayplus.domain.notice.entity.Notice;
import com.teamnative.relayplus.domain.notice.repository.NoticeRepository;
import com.teamnative.relayplus.global.exception.CustomException;
import com.teamnative.relayplus.global.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공지사항 조회 서비스
 */
@Service
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    /**
     * 공지사항 목록 조회 (페이징)
     * 최신순 정렬
     */
    public NoticeListResponse getNotices(Pageable pageable) {
        Page<Notice> page = noticeRepository.findAllByOrderByCreatedAtDesc(pageable);
        return NoticeListResponse.from(page);
    }

    /**
     * 공지사항 상세 조회
     */
    public NoticeResponse getNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));
        return NoticeResponse.from(notice);
    }
    @Transactional
    public NoticeResponse create(NoticeCreateRequest request) {
        Notice notice = Notice.builder()
                .title(request.title())
                .content(request.content())
                .build();

        Notice saved = noticeRepository.save(notice);
        return NoticeResponse.from(saved);
    }
}
