package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.base.Utils;

import java.util.Calendar;
import java.util.TimeZone;

public class GetOwnerProfileRequest {
    private final String cmd = "getOwnerProfile";
    private final long timestamp;
    private final String cookieHash;
    private final int ownerId;
    private final Integer merchantId;
    private final String devId;
    private final long tzOffset;
    private final String tzName;

    public GetOwnerProfileRequest(String cookie, int ownerId, Integer merchantId, String devId) {
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        this.tzName = TimeZone.getDefault().getID();
        this.merchantId = merchantId;
        this.ownerId = ownerId;
        this.devId = devId;
    }
}
