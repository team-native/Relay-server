package com.teamnative.relayplus.domain.auth.entity;


public enum Department {
    SW_DEVELOPMENT("소프트웨어개발과"),
    SMART_IOT("스마트IoT과"),
    AI("AI과");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
