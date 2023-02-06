package com.loyverse.dashboard.mvp.presenters;


import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.firebase.StockPushService;
import com.loyverse.dashboard.base.mvp.ProductsPresenter;
import com.loyverse.dashboard.base.mvp.ProductsView;
import com.loyverse.dashboard.base.products.ProductItem;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.Outlet;
import com.loyverse.dashboard.core.api.ProductsRequest;
import com.loyverse.dashboard.core.api.ProductsResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ProductsPresenterImpl extends ProductsPresenter<ProductsView> {
    private int outletIdFromPush = 0;

    private Action1<ProductsResponse> onNext = response -> {
        List<ProductItem> productItems = Arrays.asList(response.wares);
        dataModel.addToProductList(productItems);
        view.addToProductList(productItems);
    };

    public ProductsPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void bind(ProductsView view) {
        super.bind(view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void unbind(ProductsView view) {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.unbind(view);
    }

    @Override
    public void setUp() {
        view.setProductFilterTypeTitle(dataModel.getProductsFilterType());
        if (!provideDataFromModel()) loadData(0);
        if (stateOfSearch) {
            view.showSearch();
        }
    }

    private boolean provideDataFromModel() {
        boolean cached = false;
        if (dataModel.getProductList().size() > 0) {
            view.addToProductList(dataModel.getProductList());
            cached = true;
        }
        updateOutletToolbarState();
        return cached;
    }

    @Override
    public void loadData(int offset) {
        view.showLoadingDialog();
        if (offset == 0) {
            view.clearProductList();
            dataModel.clearProductList();
        }
        if (stateOfSearch && searchedProductName.length() == 0) {
            view.clearProductList();
            dataModel.clearProductList();
            view.hideLoadingDialog();
            return;
        }

        subscriptions.add(createOutletsPreloadObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map(outletListResponse -> {
                    //Handle push notification
                    if (outletIdFromPush != 0) {
                        changeOutletForPushNotification(outletIdFromPush);
                        updateOutletToolbarState();
                        outletIdFromPush = 0;
                    }

                    return outletListResponse;
                })
                .filter(outletListResponse -> {
                    if (!isSelectedOutletsValid(dataModel.getSelectedOutlets(), dataModel.getOutletList())) {
                        //Clear data if selected outlets are not valid
                        dataModel.clearProductList();
                        view.clearProductList();
                        view.hideLoadingDialog();
                        return false;
                    }
                    return true;
                })
                .observeOn(Schedulers.io())
                .flatMap(outletListResponse -> Observable.fromCallable(() -> server.sendRequest(createRequest(offset, isStateOfSearch() ? searchedProductName : ""), ProductsResponse.class)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError));
    }

    @Override
    public void setStateOfSearch(boolean stateOfSearch) {
        if (this.stateOfSearch != stateOfSearch) {
            dataModel.clearProductList();
            view.clearProductList();
        }
        super.setStateOfSearch(stateOfSearch);
    }



    private ProductsRequest createRequest(int offset, String search) {
        return new ProductsRequest(
                dataModel.getOwnerId(),
                dataModel.getCookieHash(),
                dataModel.getMerchantId(),
                offset,
                dataModel.getSalesSortBy(),
                dataModel.getSalesSortType(),
                dataModel.getProductsFilterType(),
                search,
                dataModel.getSelectedOutletIds()[0]);
    }

    private boolean isSelectedOutletsValid(List<Outlet> selected, List<Outlet> outletList) {
        for (Outlet outlet : selected) {
            if (!outletList.contains(outlet))
                return false;
        }
        return true;
    }

    @Subscribe(sticky = true)
    public void onEvent(StockPushService.StockChangedEvent event) {
        // TODO: 03.02.17 Consider right way to do it
        //Stop all requests because they are not valid
        subscriptions.clear();
        if (isOutletPreloaded()) {
            changeOutletForPushNotification(event.outletId);
            updateOutletToolbarState();
        } else outletIdFromPush = event.outletId;
        loadData(0);
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void changeOutletForPushNotification(int outletId) {
        dataModel.clearProductList();
        Outlet tmpOutlet = new Outlet();
        tmpOutlet.id = outletId;
        int index = dataModel.getOutletList().indexOf(tmpOutlet);
        if (index == -1) {
            return;
        }

        Outlet outlet = dataModel.getOutletList().get(index);
        List<Outlet> selectedOutlet = new ArrayList<>();
        selectedOutlet.add(outlet);

        dataModel.setSelectedOutlets(selectedOutlet);
    }

    @Override
    public void onProductTypeFilterTitleClick() {
        view.showProductFilterTypeDialog(dataModel.getProductsFilterType());
    }

    @Override
    public void onProductFilterChange(@Utils.ProductsFilterType String type) {
        dataModel.setProductsFilterType(type);
        //TODO: figure out why view is null here
        runIfNotNull(view, (view) -> view.setProductFilterTypeTitle(dataModel.getProductsFilterType()));
        loadData(0);
    }
}
