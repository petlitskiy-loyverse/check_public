package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.api.Outlet;

import java.util.List;

public interface OutletsView extends BaseView {
    void updateData(List<Outlet> outlets, List<Outlet> selectedOutlet);

    void cleanData();

    void close();

    boolean isIncludedPeriodOutlets();

}
