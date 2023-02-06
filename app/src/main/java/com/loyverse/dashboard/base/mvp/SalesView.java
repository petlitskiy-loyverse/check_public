package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.base.sales.BaseSalesItem;
import com.loyverse.dashboard.core.DataModel;

import java.util.List;

public interface SalesView extends BasePeriodView {
    void addToData(List<BaseSalesItem> newItems, double total);

    String getListKey();

    void clearList();

    void updateSortingView(DataModel.SortType sortBy, DataModel.SortBy sortType);
}
