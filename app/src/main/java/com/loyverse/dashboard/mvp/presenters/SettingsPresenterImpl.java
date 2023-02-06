package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.LogoutEvent;
import com.loyverse.dashboard.base.multishop.Permission;
import com.loyverse.dashboard.base.mvp.SettingsPresenter;
import com.loyverse.dashboard.base.mvp.SettingsView;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.SetPushNotificationRequest;
import com.loyverse.dashboard.core.api.SetPushNotificationResponse;

import org.greenrobot.eventbus.EventBus;

import rx.functions.Action1;


public class SettingsPresenterImpl extends SettingsPresenter<SettingsView> {
    private Action1<SetPushNotificationResponse> onNextTogglePushes = response -> {
        if (response.result.equals(ServerResult.OK.result))
            dataModel.setSendStockNotifications(!dataModel.getSendStockNotifications());
        else {
            view.toggleNotificationSetting(dataModel.getSendStockNotifications());
            view.showToastOnException(new ServerError(response.errortext));
        }
    };

    public SettingsPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void bind(SettingsView view) {
        super.bind(view);
        view.setAccountEmail(dataModel.getEmail());
        view.setStockNotificationSetting(dataModel.getSendStockNotifications());
        if (dataModel.getPermissions().contains(Permission.ACCESS_WARES))
            view.setNotificationSettingVisibility(true);
        else
            view.setNotificationSettingVisibility(false);
    }

    @Override
    public void onStockNotificationSettingChange(boolean enable, String deviceId) {
        subscriptions.add(server.request(new SetPushNotificationRequest(
                        dataModel.getOwnerId(),
                        dataModel.getCookieHash(),
                        dataModel.getMerchantId(),
                        enable,
                        deviceId),
                SetPushNotificationResponse.class, onNextTogglePushes, onError));
    }

    @Override
    public void onLogout() {
        if (view != null)
            view.showLogoutDialog();
    }

    @Override
    public void onLogoutConfirmation() {
        EventBus.getDefault().postSticky(new LogoutEvent());
    }
}
