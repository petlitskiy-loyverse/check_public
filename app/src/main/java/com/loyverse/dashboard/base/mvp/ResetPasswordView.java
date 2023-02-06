package com.loyverse.dashboard.base.mvp;

public interface ResetPasswordView extends BaseView {
    void showEmailSentDialog();

    void showEmailNonExistDialog();

    void showInvalidEmailMsg();

    void onSuccessEmailSent();
}
