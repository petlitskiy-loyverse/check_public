package com.loyverse.dashboard.base.mvp;


public interface LoginView extends BaseView {

    void login();

    void showWrongEmailOrPasswordDialog();

    void showInvalidEmailMsg();

    void showAccessRightsRequiredMsg();
}
