package com.loyverse.dashboard.core.api;

import com.google.gson.annotations.SerializedName;

public class StockPushResponse {

    @SerializedName("google.sent_time")
    public long sentTime;
    public int ownerId;
    public long from;
    public String type;
    public long timestamp;
    public Ware[] wares;
    @SerializedName("google.message_id")
    public String messageId;
    public String operation;
    public String collapseKey;

    public static class Ware {

        public String name;
        public long count;
        public int id;
    }
}


