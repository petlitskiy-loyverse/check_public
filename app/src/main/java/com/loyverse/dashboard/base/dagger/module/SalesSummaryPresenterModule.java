package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.SalesSummaryPresenter;
import com.loyverse.dashboard.base.mvp.SalesSummaryView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.SalesSummaryPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class SalesSummaryPresenterModule {
    @Provides
    @ActivityScope
    SalesSummaryPresenter<SalesSummaryView> providePresenter(Server server, DataModel dataModel) {
        return new SalesSummaryPresenterImpl(dataModel, server);
    }
}
