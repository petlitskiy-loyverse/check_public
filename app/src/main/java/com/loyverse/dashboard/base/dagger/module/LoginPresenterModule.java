package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.LoginPresenter;
import com.loyverse.dashboard.base.mvp.LoginView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.LoginPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class LoginPresenterModule {

    @Provides
    @ActivityScope
    LoginPresenter<LoginView> providePresenter(DataModel dataModel, Server server) {
        return new LoginPresenterImpl(dataModel, server);
    }
}
