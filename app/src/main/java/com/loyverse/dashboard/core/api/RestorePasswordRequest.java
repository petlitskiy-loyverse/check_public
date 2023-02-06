package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;

import java.util.Locale;

@SuppressWarnings("unused")
public class RestorePasswordRequest {
    private final String cmd = "restorePassword";
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String brandName = BuildConfig.BRAND_NAME;
    private final String lang = Locale.getDefault().getISO3Language();
    private final String email;
    private final String type = BuildConfig.TYPE;

    public RestorePasswordRequest(String email) {
        this.email = email;
    }
}
