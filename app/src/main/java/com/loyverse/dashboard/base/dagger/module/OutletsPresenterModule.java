package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.OutletsPresenter;
import com.loyverse.dashboard.base.mvp.OutletsView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.OutletsPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class OutletsPresenterModule {
    @Provides
    @ActivityScope
    public OutletsPresenter<OutletsView> providePresenter(DataModel dataModel, Server server) {
        return new OutletsPresenterImpl(dataModel, server);
    }
}
