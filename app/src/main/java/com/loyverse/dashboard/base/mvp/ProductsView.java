package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.products.ProductItem;

import java.util.List;

public interface ProductsView extends BaseView, ScrolledView, OutletsPreloadView, OnBackPressedView {
    void showProductFilterTypeDialog(@Utils.ProductsFilterType String filterType);

    void setProductFilterTypeTitle(@Utils.ProductsFilterType String filterType);

    void addToProductList(List<ProductItem> productList);

    void clearProductList();

    void showSearch();

    void hideSearch();
}
