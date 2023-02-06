package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;


public abstract class ReportsPresenter<T extends BasePeriodView> extends BasePeriodPresenter<T> {

    protected ReportsPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void loadDataForSelectedPeriod();
}
