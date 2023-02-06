package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.SalesPresenter;
import com.loyverse.dashboard.base.mvp.SalesView;
import com.loyverse.dashboard.base.sales.BaseSalesItem;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.CategoriesReportRequest;
import com.loyverse.dashboard.core.api.CategoriesReportResponse;
import com.loyverse.dashboard.core.api.MerchantsReportRequest;
import com.loyverse.dashboard.core.api.MerchantsReportResponse;
import com.loyverse.dashboard.core.api.WaresPeriodReportRequest;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

import static com.loyverse.dashboard.base.Utils.CATEGORIES_KEY;
import static com.loyverse.dashboard.base.Utils.EMPLOYEES_KEY;
import static com.loyverse.dashboard.base.Utils.WARES_KEY;

public class SalesPresenterImpl extends SalesPresenter<SalesView> {

    private PublishSubject<Integer> loadDataEvents = PublishSubject.create();
    private String listKey;
    private long savedOffset;
    private Action1<WaresPeriodReportResponse> onNextWaresPeriodReport = response -> {
        handleFillSelectDataResponse(response.fillSelectData);

        if (savedOffset == 0) clearWaresData();
        dataModel.addToWaresList(Arrays.asList(response.wares));
        dataModel.setWaresTotal(response.total.netSales);
        update(Arrays.asList(response.wares), dataModel.getWaresTotal());
    };
    private Action1<MerchantsReportResponse> onNextMerchantsReport = response -> {
        handleFillSelectDataResponse(response.fillSelectData);

        if (savedOffset == 0) clearMerchantsData();
        dataModel.addToMerchantsList(Arrays.asList(response.report));
        dataModel.setMerchantsTotal(response.totalNetSales);
        update(Arrays.asList(response.report), dataModel.getMerchantsTotal());
    };
    private Action1<CategoriesReportResponse> onNextCategoriesReport = response -> {
        handleFillSelectDataResponse(response.fillSelectData);

        if (savedOffset == 0) clearCategoriesData();
        dataModel.addToCategoriesList(Arrays.asList(response.categories));
        dataModel.setCategoriesTotal(response.totalNetSales);
        update(Arrays.asList(response.categories), dataModel.getCategoriesTotal());
    };
    private Action1<Integer> ofOffsetChange = offset -> {
        savedOffset = offset;

        this.view.showLoadingDialog();

        Observable observable;

        if (!isOutletPreloaded()) {
            observable = createOutletsPreloadObservable()
                    .flatMap(outletListResponse -> getRequestObservable(offset));
        } else {
            observable = getRequestObservable(offset)
                    .subscribeOn(Schedulers.io());
        }
        observable = observable.observeOn(AndroidSchedulers.mainThread());

        switch (listKey) {
            case WARES_KEY:
                if (offset == 0) clearWaresData();
                subscriptions.add(observable.subscribe(onNextWaresPeriodReport, onError));
                break;
            case EMPLOYEES_KEY:
                if (offset == 0) clearMerchantsData();
                subscriptions.add(observable.subscribe(onNextMerchantsReport, onError));
                break;
            case CATEGORIES_KEY:
                if (offset == 0) clearCategoriesData();
                subscriptions.add(observable.subscribe(onNextCategoriesReport, onError));
                break;
            default:
                this.view.hideLoadingDialog();
        }
    };

    public SalesPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void bind(SalesView view) {
        super.bind(view);
        Subscription subscription = loadDataEvents.debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ofOffsetChange, Timber::e);
        subscriptions.add(subscription);

        listKey = this.view.getListKey();
        if (!provideDataFromModel()) loadData(0);
    }

    private boolean provideDataFromModel() {
        boolean cached = false;
        view.updateSortingView(dataModel.getSalesSortType(), dataModel.getSalesSortBy());
        switch (listKey) {
            case WARES_KEY:
                if (dataModel.getWaresList().size() != 0) {
                    view.addToData(dataModel.getWaresList(), dataModel.getWaresTotal());
                    cached = true;
                }
                break;
            case EMPLOYEES_KEY:
                if (dataModel.getMerchantsList().size() != 0) {
                    view.addToData(dataModel.getMerchantsList(), dataModel.getMerchantsTotal());
                    cached = true;
                }
                break;
            case CATEGORIES_KEY:
                if (dataModel.getCategoriesList().size() != 0) {
                    view.addToData(dataModel.getCategoriesList(), dataModel.getCategoriesTotal());
                    cached = true;
                }
                break;
        }
        updatePeriodText();
        updateOutletToolbarState();

        return cached;

    }

    private void loadData(int offset) {
        updatePeriodText();
        loadDataEvents.onNext(offset);
    }

    private void clearAllData(){
        dataModel.clearWaresList();
        dataModel.clearCategoriesList();
        dataModel.clearMerchantsList();
    }

    @Override
    public void loadNext() {
        final int offset;
        switch (listKey) {
            case WARES_KEY:
                offset = dataModel.getWaresList().size();
                break;
            case EMPLOYEES_KEY:
                offset = dataModel.getMerchantsList().size();
                break;
            case CATEGORIES_KEY:
                offset = dataModel.getCategoriesList().size();
                break;
            default:
                offset = 0;
        }
        loadData(offset);
    }

    @Override
    public void refreshData() {
        loadData(0);
    }

    private Observable getRequestObservable(int offset) {
        switch (listKey) {
            case WARES_KEY:
                return createWaresResponseObservable(offset);
            case EMPLOYEES_KEY:
                return createMerchantResponseObservable(offset);
            case CATEGORIES_KEY:
                return createCategoriesResponseObservable(offset);
            default:
                return Observable.empty();
        }
    }

    private void clearWaresData() {
        view.clearList();
        dataModel.clearWaresList();
    }

    private void clearMerchantsData() {
        view.clearList();
        dataModel.clearMerchantsList();
    }

    private void clearCategoriesData() {
        view.clearList();
        dataModel.clearCategoriesList();
    }

    @Override
    public void onNameSortChange() {
        view.clearList();
        if (dataModel.getSalesSortBy() == DataModel.SortBy.NAME) {
            dataModel.setSalesSortType(dataModel.getSalesSortType().invert());
        } else {
            dataModel.setSalesSortBy(DataModel.SortBy.NAME);
            dataModel.setSalesSortType(Utils.getDefaultSortTypeFor(DataModel.SortBy.NAME));
        }
        clearAllData();
        loadData(0);
        view.updateSortingView(dataModel.getSalesSortType(), dataModel.getSalesSortBy());
    }

    @Override
    public void onNetSortChange() {
        view.clearList();
        if (dataModel.getSalesSortBy() == DataModel.SortBy.NET) {
            dataModel.setSalesSortType(dataModel.getSalesSortType().invert());
        } else {
            dataModel.setSalesSortBy(DataModel.SortBy.NET);
            dataModel.setSalesSortType(Utils.getDefaultSortTypeFor(DataModel.SortBy.NET));
        }
        clearAllData();
        loadData(0);
        view.updateSortingView(dataModel.getSalesSortType(), dataModel.getSalesSortBy());
    }

    @Override
    public void prevPeriod() {
        super.prevPeriod();
        loadData(0);
    }

    @Override
    public void nextPeriod() {
        super.nextPeriod();
        loadData(0);
    }

    @Override
    public void nonCustomPeriodUnitSelected(int period) {
        super.nonCustomPeriodUnitSelected(period);
        loadData(0);
    }

    @Override
    public void customPeriodUnitSelected(List<Date> dates,Integer startTime,Integer endTime) {
        super.customPeriodUnitSelected(dates,startTime,endTime);
        loadData(0);
    }

    private Observable<WaresPeriodReportResponse> createWaresResponseObservable(int offset) {
        return Observable.fromCallable(() -> server.sendRequest(new WaresPeriodReportRequest(
                        dataModel.getOwnerId(),
                        dataModel.getMerchantId(),
                        offset,
                        dataModel.getFromDate(),
                        dataModel.getToDate(),
                        dataModel.getSalesSortBy(),
                        dataModel.getSalesSortType(),
                        dataModel.getCookieHash(),
                        dataModel.getSelectedOutletIds(),dataModel.getStartTime(), dataModel.getEndTime()),
                WaresPeriodReportResponse.class));
    }

    private Observable<CategoriesReportResponse> createCategoriesResponseObservable(int offset) {
        return Observable.fromCallable(() -> server.sendRequest(new CategoriesReportRequest(
                        dataModel.getOwnerId(),
                        dataModel.getMerchantId(),
                        offset,
                        dataModel.getFromDate(),
                        dataModel.getToDate(),
                        dataModel.getSalesSortBy(),
                        dataModel.getSalesSortType(),
                        dataModel.getCookieHash(),
                        dataModel.getSelectedOutletIds(),dataModel.getStartTime(), dataModel.getEndTime()),
                CategoriesReportResponse.class));
    }

    private Observable<MerchantsReportResponse> createMerchantResponseObservable(int offset) {
        return Observable.fromCallable(() -> server.sendRequest(new MerchantsReportRequest(
                        dataModel.getOwnerId(),
                        dataModel.getMerchantId(),
                        offset, //offset
                        dataModel.getFromDate(),
                        dataModel.getToDate(),
                        dataModel.getSalesSortBy(),
                        dataModel.getSalesSortType(),
                        dataModel.getCookieHash(),
                        dataModel.getSelectedOutletIds(), dataModel.getStartTime(), dataModel.getEndTime()),
                MerchantsReportResponse.class));
    }

    private void update(List<BaseSalesItem> items, double total) {
        view.addToData(items, total);
        view.hideLoadingDialog();
    }
}
