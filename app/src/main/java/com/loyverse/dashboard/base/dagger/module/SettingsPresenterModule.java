package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.SettingsPresenter;
import com.loyverse.dashboard.base.mvp.SettingsView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.SettingsPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsPresenterModule {

    @Provides
    @ActivityScope
    public SettingsPresenter<SettingsView> providePresenter(DataModel model, Server server) {
        return new SettingsPresenterImpl(model, server);
    }
}
