package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public abstract class SalesPresenter<T extends BasePeriodView> extends BasePeriodPresenter<T> {

    protected SalesPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void loadNext();

    public abstract void refreshData();

    public abstract void onNameSortChange();

    public abstract void onNetSortChange();
}
