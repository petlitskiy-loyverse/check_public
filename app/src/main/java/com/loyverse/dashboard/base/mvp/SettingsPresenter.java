package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public abstract class SettingsPresenter<T extends SettingsView> extends BasePresenter<T> {
    protected SettingsPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void onStockNotificationSettingChange(boolean enable, String deviceId);

    public abstract void onLogout();

    public abstract void onLogoutConfirmation();

}
