package com.teamnative.relayplus.domain.notice.dto;

import com.teamnative.relayplus.domain.notice.entity.Notice;

import java.time.LocalDateTime;

/**
 * 공지사항 상세 조회 응답 DTO
 */
public record NoticeResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NoticeResponse from(Notice notice) {
        return new NoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        );
    }
}
