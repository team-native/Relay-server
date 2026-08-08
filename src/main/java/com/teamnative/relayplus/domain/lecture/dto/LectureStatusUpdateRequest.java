package com.teamnative.relayplus.domain.lecture.dto;

import com.teamnative.relayplus.domain.lecture.entity.LectureStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 게시글 상태 변경 요청 DTO. ADMIN 권한을 가진 사용자만 호출할 수 있습니다.
 */
public record LectureStatusUpdateRequest(
        @NotNull(message = "변경할 상태를 입력해주세요.")
        LectureStatus status
) {
}
