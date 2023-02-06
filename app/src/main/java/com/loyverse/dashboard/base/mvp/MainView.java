package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.api.GetOwnerProfileResponse;

public interface MainView extends BaseView {
    void showBottomBar(boolean animated);

    void hideBottomBar(boolean animated);

    void setTabs(@MainPresenter.TabType int[] tabs);

    void showTab(@MainPresenter.TabType int tab);

    void showLoginScreen();

    void showBillingLockScreen();

    void showRetryDialog();

    void onRetryClick();

    @MainPresenter.TabType
    int getCurrentTab();

    String getDeviceId();

    void showNoInternetRetryDialog();

    void setProfileData(GetOwnerProfileResponse response);
}
