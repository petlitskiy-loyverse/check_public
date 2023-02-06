package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.MainPresenter;
import com.loyverse.dashboard.base.mvp.MainView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.MainPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class MainPresenterModule {

    @Provides
    @ActivityScope
    MainPresenter<MainView> providePresenter(DataModel dataModel, Server server) {
        return new MainPresenterImpl(dataModel, server);
    }
}
