package com.loyverse.dashboard.base.dagger.module;

import android.content.Context;

import com.loyverse.dashboard.core.DataModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DataModelModule {
    private Context context;

    public DataModelModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    public DataModel getModel() {
        return new DataModel(context);
    }
}
