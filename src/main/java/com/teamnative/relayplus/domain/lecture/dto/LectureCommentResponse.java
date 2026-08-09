package com.teamnative.relayplus.domain.lecture.dto;

import com.teamnative.relayplus.domain.lecture.entity.LectureComment;
import com.teamnative.relayplus.global.util.TimeAgoFormatter;

/**
 * 댓글 응답 DTO
 * authorGeneration은 숫자(8/9/10)로, authorDepartment는 "소프트웨어개발과" 같은 표시용 문자열로 내려갑니다.
 * timeAgo는 "방금 전 / N분 전 / N시간 전 / N일 전 / yyyy.MM.dd" 형태의 상대 시간입니다.
 */
public record LectureCommentResponse(
        Long id,
        String authorName,
        int authorGeneration,
        String authorDepartment,
        String content,
        String timeAgo
) {
    public static LectureCommentResponse from(LectureComment comment) {
        return new LectureCommentResponse(
                comment.getId(),
                comment.getAuthor().getName(),
                comment.getAuthor().getGeneration().getNumber(),
                comment.getAuthor().getDepartment().getDisplayName(),
                comment.getContent(),
                TimeAgoFormatter.format(comment.getCreatedAt())
        );
    }
}
