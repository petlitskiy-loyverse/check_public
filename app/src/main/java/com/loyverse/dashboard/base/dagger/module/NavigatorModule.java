package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.core.Navigator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NavigatorModule {


    @Provides
    @Singleton
    Navigator addNavigator() {
        return new Navigator();
    }
}