package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class OkHttpClientModule {

    @Provides
    @Singleton
    OkHttpClient provideClient() {
        return Utils.createHttpClient();
    }
}