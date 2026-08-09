package com.teamnative.relayplus.domain.notice.dto;

import com.teamnative.relayplus.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 공지사항 목록 조회 응답 DTO
 */
public record NoticeListResponse(
        List<NoticeItem> notices,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public record NoticeItem(
            Long id,
            String title,
            LocalDateTime createdAt
    ) {
        public static NoticeItem from(Notice notice) {
            return new NoticeItem(notice.getId(), notice.getTitle(), notice.getCreatedAt());
        }
    }

    public static NoticeListResponse from(Page<Notice> page) {
        List<NoticeItem> items = page.getContent()
                .stream()
                .map(NoticeItem::from)
                .toList();

        return new NoticeListResponse(
                items,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
