package com.loyverse.dashboard.core.api;

import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

@SuppressWarnings("unused")
public class ProductsRequest {
    public final String cmd = "getshortwares";
    public final int ver = BuildConfig.SERVER_VERSION;
    public final String brandName = BuildConfig.BRAND_NAME;
    public final int ownerId;
    public final int limit;
    public final int offset;
    public final String sortBy;
    public final String sortType;
    public final String filterType;
    public final String cookieHash;
    public final String search;
    public final long timestamp;
    public final long outletId;
    public final Integer merchantId;
    public final String type = BuildConfig.TYPE;

    public ProductsRequest(int ownerId, String cookie, Integer merchantId, int offset, DataModel.SortBy sortBy, DataModel.SortType sortType, String filterType, String search, long outletId) {
        this.ownerId = ownerId;
        this.merchantId = merchantId;
        this.filterType = filterType;
        this.search = search;
        this.limit = Server.LIMIT;
        this.offset = offset;
        this.sortBy = sortBy.toString();
        this.sortType = sortType.toString();
        this.timestamp = System.currentTimeMillis();
        this.cookieHash = Utils.MD5(cookie + timestamp);
        this.outletId = outletId;
    }
}
