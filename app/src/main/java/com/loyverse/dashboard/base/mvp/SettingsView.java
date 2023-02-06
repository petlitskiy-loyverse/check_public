package com.loyverse.dashboard.base.mvp;

public interface SettingsView extends BaseView {
    void setStockNotificationSetting(boolean value);

    void setAccountEmail(String email);

    void showLogoutDialog();

    void logout();

    void onLogoutConfirmation();

    void toggleNotificationSetting(boolean enable);

    void setNotificationSettingVisibility(boolean enable);
}
