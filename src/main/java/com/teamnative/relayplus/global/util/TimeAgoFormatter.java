package com.teamnative.relayplus.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * "방금 전 / N분 전 / N시간 전 / N일 전" 형태의 상대 시간 문자열을 만들어주는 유틸입니다.
 * 댓글 등 작성 시간을 사용자 친화적으로 보여줄 때 사용합니다.
 */
public final class TimeAgoFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private TimeAgoFormatter() {
    }

    public static String format(LocalDateTime target) {
        return format(target, LocalDateTime.now());
    }

    public static String format(LocalDateTime target, LocalDateTime now) {
        Duration duration = Duration.between(target, now);
        long seconds = duration.getSeconds();

        if (seconds < 60) {
            return "방금 전";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "분 전";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "시간 전";
        }
        long days = hours / 24;
        if (days < 7) {
            return days + "일 전";
        }
        return target.format(DATE_FORMATTER);
    }
}
