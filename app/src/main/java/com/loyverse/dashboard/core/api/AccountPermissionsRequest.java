package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;

@SuppressWarnings("unused")
public class AccountPermissionsRequest {
    private final String cmd = "getAccountPermissions";
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String brandName = BuildConfig.BRAND_NAME;
    private final String type = BuildConfig.TYPE;
    private final int ownerId;
    private final Integer merchantId;
    private final String cookieHash;
    private final long timestamp;


    public AccountPermissionsRequest(int ownerId, Integer merchantId, String cookie) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
    }
}
