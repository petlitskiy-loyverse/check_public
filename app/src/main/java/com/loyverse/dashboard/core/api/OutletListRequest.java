package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;

@SuppressWarnings("unused")
public class OutletListRequest {
    private final String cmd = "getShortOutlets";
    private final int ownerId;
    private final Integer merchantId;
    private final long timestamp;
    private final String cookieHash;
    private final long fromDate;
    private final long toDate;
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String brandName = BuildConfig.BRAND_NAME;
    //IT MUST NOT CONTAIN "type"

    public OutletListRequest(int ownerId, String cookie, Integer merchantId, long fromDate, long toDate) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
