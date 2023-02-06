package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;


public abstract class ResetPasswordPresenter<T extends BaseView> extends BasePresenter<T> {

    protected ResetPasswordPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void resetPassword(String email);
}
