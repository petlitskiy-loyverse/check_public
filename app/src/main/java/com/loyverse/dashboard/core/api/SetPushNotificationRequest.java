package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;

public class SetPushNotificationRequest {
    public final Integer merchantId;
    private final String cmd = "setpushnotification";
    private final int ownerId;
    private final String cookieHash;
    private final boolean pushNotificationEnable;
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String type = BuildConfig.TYPE;
    private final String devId;
    private final long timestamp;

    public SetPushNotificationRequest(int ownerId, String cookie, Integer merchantId, boolean enable, String devId) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.pushNotificationEnable = enable;
        this.devId = devId;
    }
}
