package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public abstract class ProductsPresenter<T extends OutletsPreloadView> extends OutletPreloadPresenter<T> {
    protected String searchedProductName = "";

    protected boolean stateOfSearch = false;

    protected ProductsPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract void loadData(int offset);

    public abstract void onProductTypeFilterTitleClick();

    public abstract void onProductFilterChange(@Utils.ProductsFilterType String type);

    public abstract void setUp();

    public boolean isStateOfSearch() {
        return stateOfSearch;
    }

    public void setStateOfSearch(boolean stateOfSearch) {
        this.stateOfSearch = stateOfSearch;
    }

    public String getSearchedProductName() {
        return searchedProductName;
    }

    public void setSearchedProductName(String searchedProductName) {
        this.searchedProductName = searchedProductName;
    }
}
