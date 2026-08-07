package com.teamnative.relayplus.domain.auth.validation;

public final class AuthPattern {

    public static final String SCHOOL_EMAIL = "^s\\d{5}@gsm\\.hs\\.kr$";

    public static final String PASSWORD =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,64}$";

    public static final String VERIFICATION_CODE = "^\\d{6}$";

    private AuthPattern() {
    }
}
