package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.SalesPresenter;
import com.loyverse.dashboard.base.mvp.SalesView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.SalesPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class SalesPresenterModule {

    @Provides
    @ActivityScope
    SalesPresenter<SalesView> providePresenter(Server server, DataModel dataModel) {
        return new SalesPresenterImpl(dataModel, server);
    }
}
