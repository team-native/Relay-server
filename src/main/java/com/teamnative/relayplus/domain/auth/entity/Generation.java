package com.teamnative.relayplus.domain.auth.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Generation {
    EIGHTH(8),
    NINTH(9),
    TENTH(10);

    private final int number;

    Generation(int number) {
        this.number = number;
    }

    /**
     * JSON 직렬화할 때 숫자 값으로 변환
     * TENTH → 10
     */
    @JsonValue
    public int getNumber() {
        return number;
    }

    /**
     * JSON 역직렬화할 때 숫자에서 enum으로 변환
     * generation: 10 → TENTH
     */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Generation fromNumber(int number) {
        for (Generation gen : Generation.values()) {
            if (gen.number == number) {
                return gen;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 기수입니다: " + number + " (8, 9, 10만 가능)");
    }
}