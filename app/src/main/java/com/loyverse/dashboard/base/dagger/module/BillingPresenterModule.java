package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.BillingPresenter;
import com.loyverse.dashboard.base.mvp.BillingView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.BillingPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class BillingPresenterModule {

    @Provides
    @ActivityScope
    BillingPresenter<BillingView> providePresenter(DataModel dataModel, Server server) {
        return new BillingPresenterImpl(dataModel, server);
    }

}
