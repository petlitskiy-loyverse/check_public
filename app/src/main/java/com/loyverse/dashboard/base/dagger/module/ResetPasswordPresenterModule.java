package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.ResetPasswordPresenter;
import com.loyverse.dashboard.base.mvp.ResetPasswordView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.ResetPasswordPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ResetPasswordPresenterModule {

    @Provides
    @ActivityScope
    ResetPasswordPresenter<ResetPasswordView> providePresenter(Server server, DataModel dataModel) {
        return new ResetPasswordPresenterImpl(dataModel, server);
    }
}
