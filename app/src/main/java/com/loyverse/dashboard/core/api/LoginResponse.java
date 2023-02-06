package com.loyverse.dashboard.core.api;

@SuppressWarnings("unused")
public class LoginResponse extends BaseResponsePOJO {
    public String cookie;
    public int ownerId;
    public int merchantId;
    public String[] permissions;
}

