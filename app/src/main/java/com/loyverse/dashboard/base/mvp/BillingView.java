package com.loyverse.dashboard.base.mvp;

public interface BillingView extends BaseView {

    void goToBillingView();

    void showButtonBilling();

    void hideButtonBilling();

    void comeBackToLoginMenu();

    void exitFromBilling();

    void showBillingExitDialog();
}
