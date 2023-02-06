package com.loyverse.dashboard.base.dagger.module;

import com.google.gson.GsonBuilder;
import com.loyverse.dashboard.core.Server;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class ServerModule {

    @Provides
    @Singleton
    Server addServer(OkHttpClient client) {
        return new Server(new GsonBuilder().disableHtmlEscaping().create(), client);
    }
}
