package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

import java.util.Calendar;
import java.util.TimeZone;

@SuppressWarnings("unused")
public class CategoriesReportRequest {
    private Integer startTime;
    private Integer endTime;
    private final String cmd = "categoriesReport";
    private final String type = BuildConfig.TYPE;
    private final int ownerId;
    private final Integer merchantId;
    private final int limit;
    private final int offset;
    private final long from;
    private final long to;
    private final long timestamp;
    private final String sortBy;
    private final String sortType;
    private final String cookieHash;
    private final long tzOffset;
    private final String tzName;
    private final long[] outletsIds;


    public CategoriesReportRequest(int ownerId, Integer merchantId, int offset, long from, long to, DataModel.SortBy sortBy, DataModel.SortType sortType, String cookie, long[] outletsIds,Integer startTime,Integer endTime) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.limit = Server.LIMIT;
        this.offset = offset;
        this.from = from;
        this.to = to;
        this.sortBy = sortBy.toString();
        this.sortType = sortType.toString();
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.tzOffset = Calendar.getInstance().getTimeZone().getRawOffset();
        this.tzName = TimeZone.getDefault().getID();
        this.outletsIds = outletsIds;
        this.startTime=startTime;
        this.endTime=endTime;
    }

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
}


