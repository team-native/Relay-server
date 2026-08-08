package com.teamnative.relayplus.domain.lecture.entity;

/**
 * 릴레이 스터디 게시글의 진행 상태입니다.
 * 상태 전환은 자동으로 이루어지지 않으며, ADMIN 권한을 가진 사용자가
 * PATCH /api/lectures/{id}/status API를 통해 수동으로 변경합니다.
 */
public enum LectureStatus {

    /** 개설 미정: 아직 개설이 확정되지 않은 상태 (기본값) */
    PENDING,

    /** 개설 확정: 개설이 확정되어 신청(모집)중이거나, 신청 마감 후 연사 진행 전인 상태 */
    CONFIRMED,

    /** 종료: 연사가 종료된 상태 */
    CLOSED
}
