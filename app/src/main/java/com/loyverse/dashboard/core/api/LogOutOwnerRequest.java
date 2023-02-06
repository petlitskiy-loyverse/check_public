package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;

@SuppressWarnings("unused")
public class LogOutOwnerRequest {
    private final String cmd = "logOutOwner";
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String brandName = BuildConfig.BRAND_NAME;
    private final String type = BuildConfig.TYPE;
    private final long timestamp;
    private final String cookieHash;
    private final int ownerId;
    private final Integer merchantId;
    private final String devId;


    public LogOutOwnerRequest(String cookie, int ownerId, Integer merchantId, String devId) {
        this.merchantId = merchantId;
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.ownerId = ownerId;
        this.devId = devId;
    }
}
