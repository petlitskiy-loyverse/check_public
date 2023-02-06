package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.ReportsPresenter;
import com.loyverse.dashboard.base.mvp.ReportsView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.ReportsPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ReportsPresenterModule {

    @Provides
    @ActivityScope
    ReportsPresenter<ReportsView> providePresenter(Server server, DataModel dataModel) {
        return new ReportsPresenterImpl(dataModel, server);
    }
}
