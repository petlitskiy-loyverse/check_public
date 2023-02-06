package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.Outlet;

public abstract class OutletsPresenter<T extends OutletsView> extends BasePresenter<T> {
    protected OutletsPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void onOutletSelected(Outlet outlet);

    public abstract void loadData();
}
