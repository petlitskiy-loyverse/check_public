package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.Server;

import java.util.Calendar;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class EarningsReportRequest {
    private Integer startTime;

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    private Integer endTime;
    private final String cmd = "earningsReport";
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String brandName = BuildConfig.BRAND_NAME;
    private final String type = BuildConfig.TYPE;
    private final int ownerId;
    private final Integer merchantId;
    private final long timestamp;
    private final String cookieHash;
    private final String divider;
    private final int offset;
    private final int limit;
    private final long startDate;
    private final long endDate;
    private final long tzOffset;
    private final String tzName;
    private final String compareBy;
    private final long[] outletsIds;


    public EarningsReportRequest(int ownerId, String cookie, Integer merchantId, String divider, long startDate, long endDate, String compareBy, long[] outletsIds,Integer startTime,Integer endTime) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.divider = divider;
        this.offset = Server.LIMIT;
        this.limit = Server.LIMIT;
        this.startDate = startDate;
        this.endDate = endDate;
        this.tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        this.tzName = TimeZone.getDefault().getID();
        this.compareBy = compareBy;
        this.outletsIds = outletsIds;
        this.startTime=startTime;
        this.endTime=endTime;
    }
}
