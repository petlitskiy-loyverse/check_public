package com.loyverse.dashboard.base.dagger;

import com.loyverse.dashboard.base.dagger.module.DataModelModule;
import com.loyverse.dashboard.base.dagger.module.NavigatorModule;
import com.loyverse.dashboard.base.dagger.module.OkHttpClientModule;
import com.loyverse.dashboard.base.dagger.module.ServerModule;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Navigator;
import com.loyverse.dashboard.core.Server;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;

@Singleton
@Component(modules = {
        ServerModule.class,
        NavigatorModule.class,
        OkHttpClientModule.class,
        DataModelModule.class
})
public interface ApplicationComponent {
    Server server();

    OkHttpClient httpClient();

    DataModel dataModel();

    Navigator navigator();

}
