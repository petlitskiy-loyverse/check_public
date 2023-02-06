package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;

/**
 * Created by Aleksandr on 1/30/2018.
 */
//FIXME: REMOVE, IT IS USELESS!!!
@SuppressWarnings("unused")
public class VariantsPeriodReportResponse {

    private final String cmd = "waresPeriodReport";
    private final int ownerId;
    private final Integer merchantId;
    private final int limit;
    private final int offset;
    private final long startDate;
    private final long endDate;
    private final String sortBy;
    private final String sortType;
    private final String cookieHash;
    private final long timestamp;
    private final long tzOffset;
    private final long[] outletsIds;
    private final String type = BuildConfig.TYPE;

    public VariantsPeriodReportResponse(int ownerId, Integer merchantId, int limit, int offset, long startDate, long endDate, String sortBy, String sortType, String cookieHash, long timestamp, long tzOffset, long[] outletsIds) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.limit = limit;
        this.offset = offset;
        this.startDate = startDate;
        this.endDate = endDate;
        this.sortBy = sortBy;
        this.sortType = sortType;
        this.cookieHash = cookieHash;
        this.timestamp = timestamp;
        this.tzOffset = tzOffset;
        this.outletsIds = outletsIds;
    }
}
