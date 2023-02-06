package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public abstract class LoginPresenter<T extends BaseView> extends BasePresenter<T> {

    protected LoginPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void handleLogin(String email, String password, String deviceId, String deviceName);

    public abstract void handleIfAlreadyLoggedIn();

}