package com.loyverse.dashboard.core.api;


import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.Server;

@SuppressWarnings("unused")
public class LoginRequest {
    private final String cmd = "loginDashboard";
    private final int ver = BuildConfig.SERVER_VERSION;
    private final String brandName = BuildConfig.BRAND_NAME;
    private final String email;
    private final String passwdHash;
    private final String passwordEncoded;
    private final String devId;
    private final String devName;
    private final String type = BuildConfig.TYPE;
    private final String gcmId;
    private final String devType = Utils.DEVICE_TYPE;

    public LoginRequest(String email, String password, String devId, String devName, String gcmId) {
        this.email = email;
        this.passwdHash = Utils.encryptWithSHA1andRSA(password, Server.PASSWORD_RSA_PUBLIC_KEY);
        this.passwordEncoded = Utils.encryptWithRSA(password, Server.PASSWORD_RSA_PUBLIC_KEY);
        this.devId = devId;
        this.devName = devName;
        this.gcmId = gcmId;
    }

}
