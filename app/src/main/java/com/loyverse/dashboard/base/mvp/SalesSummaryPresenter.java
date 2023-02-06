package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public abstract class SalesSummaryPresenter<T extends BasePeriodView> extends BasePeriodPresenter<T> {


    protected SalesSummaryPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void loadData();
}
