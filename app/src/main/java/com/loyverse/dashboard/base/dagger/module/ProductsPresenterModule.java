package com.loyverse.dashboard.base.dagger.module;

import com.loyverse.dashboard.base.dagger.ActivityScope;
import com.loyverse.dashboard.base.mvp.ProductsPresenter;
import com.loyverse.dashboard.base.mvp.ProductsView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.ProductsPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class ProductsPresenterModule {
    @Provides
    @ActivityScope
    ProductsPresenter<ProductsView> providePresenter(Server server, DataModel dataModel) {
        return new ProductsPresenterImpl(dataModel, server);
    }
}
