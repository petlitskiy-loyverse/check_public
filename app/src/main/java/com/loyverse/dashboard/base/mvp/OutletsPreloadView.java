package com.loyverse.dashboard.base.mvp;

public interface OutletsPreloadView extends BaseView {
    void setOutletName(String shopName);

    void showOutletTitleAndOutletsIcon();

    void hideOutletTitleAndOutletsIcon();
}
