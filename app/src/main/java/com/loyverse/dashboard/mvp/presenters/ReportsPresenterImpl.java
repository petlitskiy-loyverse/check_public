package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.PeriodUtils;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.ReportsPresenter;
import com.loyverse.dashboard.base.mvp.ReportsView;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.CategoriesReportRequest;
import com.loyverse.dashboard.core.api.CategoriesReportResponse;
import com.loyverse.dashboard.core.api.EarningsReportRequest;
import com.loyverse.dashboard.core.api.EarningsReportResponse;
import com.loyverse.dashboard.core.api.MerchantsReportRequest;
import com.loyverse.dashboard.core.api.MerchantsReportResponse;
import com.loyverse.dashboard.core.api.WaresPeriodReportRequest;
import com.loyverse.dashboard.core.api.WaresPeriodReportResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.CompositeException;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class ReportsPresenterImpl extends ReportsPresenter<ReportsView> {
    private Func1<Throwable, Observable> onCompositeError = throwable -> {
        if (throwable instanceof CompositeException) {
            for (Throwable throwable1 :
                    ((CompositeException) throwable).getExceptions()) {
                onError.call(throwable1);
            }
        } else onError.call(throwable);
        return Observable.empty();
    };

    private PublishSubject<Boolean> refreshDataSubject = PublishSubject.create();

    private Action1<Throwable> onAccessDenyError = throwable -> {
        if (throwable instanceof CompositeException) {
            for (Throwable throwable1 : ((CompositeException) throwable).getExceptions()) {
                if ((throwable1 instanceof ServerError)
                        && ((ServerError) throwable1).getResult().equals(ServerResult.ACCESS_DENIED.result)) {
                    DataModel.DashboardChartData chartData = new DataModel.DashboardChartData();
                    chartData.setPercentageDifference(0);
                    chartData.setTotalValue(0);

                    view.updateOperationChart(chartData);
                    view.updateAverageTicketChart(chartData);
                    view.updateTotalSalesChart(chartData);
                    view.clearLists();
                }
            }
        }
    };

    private Action1<WaresPeriodReportResponse> onNextWaresPeriodReport = response -> {
        view.hideLoadingDialog();
        dataModel.setTopWares(response.top5);
        view.updateProductsReport(Arrays.asList(dataModel.getTopWares()));
    };
    private Action1<MerchantsReportResponse> onNextMerchantsReport = response -> {
        view.hideLoadingDialog();
        dataModel.setTopMerchants(response.top5);
        view.updateEmployeesReport(Arrays.asList(dataModel.getTopMerchants()));
    };
    private Action1<CategoriesReportResponse> onNextCategoriesReport = response -> {
        view.hideLoadingDialog();
        dataModel.setTopCategories(response.top5);
        view.updateCategoriesReport(Arrays.asList(dataModel.getTopCategories()));
    };
    //todo cleanup
    private Action1<EarningsReportResponse> onNextEarningsReport = response -> {
        view.hideLoadingDialog();
        dataModel.setEarningsData(response.earningsRows);
        dataModel.setHideFields(response.hideFields);
        handleFillSelectDataResponse(response.fillSelectData);

        view.updateEarningsView(response.earningsRows, response.divider);

        EarningsReportResponse.TotalValues values = response.totalValues;

        DataModel.DashboardChartData operationsChart = new DataModel.DashboardChartData();
        operationsChart.setTotalValue(values.totalReceiptsCount);
        operationsChart.setPercentageDifference(Utils.calculatePercentageIncrease(
                values.totalReceiptsCount, values.totalReceiptsCountBefore));
        dataModel.setOperationsChartData(operationsChart);
        view.updateOperationChart(operationsChart);

        DataModel.DashboardChartData totalSalesChart = new DataModel.DashboardChartData();
        totalSalesChart.setTotalValue(values.totalPeriodEarningsSum);
        totalSalesChart.setPercentageDifference(Utils.calculatePercentageIncrease(
                values.totalPeriodEarningsSum, values.totalPeriodEarningsSumBefore));
        dataModel.setTotalSalesChartData(totalSalesChart);
        view.updateTotalSalesChart(totalSalesChart);

        //Server returns wrong calculation (c) Vania
        long avgTicket = 0;
        if (values.totalReceiptsCount != 0) {
            avgTicket = values.totalPeriodEarningsSum / values.totalReceiptsCount;
        }
        long avgTicketBefore = 0;
        if (values.totalReceiptsCountBefore != 0) {
            avgTicketBefore = values.totalPeriodEarningsSumBefore / values.totalReceiptsCountBefore;
        }
        DataModel.DashboardChartData avgTicketChart = new DataModel.DashboardChartData();
        avgTicketChart.setTotalValue(avgTicket);
        avgTicketChart.setPercentageDifference(Utils.calculatePercentageIncrease(
                avgTicket, avgTicketBefore));
        dataModel.setAvgTicketChartData(avgTicketChart);
        view.updateAverageTicketChart(avgTicketChart);
    };

    private Action1<Boolean> onRefreshEvent = d -> {
        dataModel.clearPeriodLists();
        view.clearLists();
        view.showLoadingDialog();

        ConnectableObservable observable;
        if (!isOutletPreloaded()) {
            observable = createOutletsPreloadObservable()
                    .flatMap(outletListResponse -> createRequestObservable())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(onAccessDenyError)
                    .onErrorResumeNext(onCompositeError)
                    .publish();
        } else {
            observable = createRequestObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError(onAccessDenyError)
                    .onErrorResumeNext(onCompositeError)
                    .publish();
        }

        subscriptions.add(observable
                .filter(o -> o instanceof EarningsReportResponse)
                .subscribe(onNextEarningsReport, onError));
        subscriptions.add(observable
                .filter(o -> o instanceof WaresPeriodReportResponse)
                .subscribe(onNextWaresPeriodReport, onError));
        subscriptions.add(observable
                .filter(o -> o instanceof MerchantsReportResponse)
                .subscribe(onNextMerchantsReport, onError));
        subscriptions.add(observable
                .filter(o -> o instanceof CategoriesReportResponse)
                .subscribe(onNextCategoriesReport, onError));

        subscriptions.add(observable.connect());
    };


    public ReportsPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void bind(ReportsView view) {
        super.bind(view);
        Subscription subscription = refreshDataSubject.debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onRefreshEvent, Timber::e);
        subscriptions.add(subscription);

        if (!provideDataFromModel()) loadDataForSelectedPeriod();
    }

    private boolean provideDataFromModel() {
        //todo add graph data
        boolean cached = true;
        if (dataModel.getTopMerchants().length == 0 || dataModel.getTopCategories().length == 0 || dataModel.getTopWares().length == 0)
            cached = false;
        if (dataModel.getEarningsData().length == 0 || dataModel.getOperationsChartData() == null || dataModel.getTotalSalesChartData() == null || dataModel.getAvgTicketChartData() == null)
            cached = false;
        if (cached) {
            view.updateProductsReport(Arrays.asList(dataModel.getTopWares()));
            view.updateCategoriesReport(Arrays.asList(dataModel.getTopCategories()));
            view.updateEmployeesReport(Arrays.asList(dataModel.getTopMerchants()));
            view.updateEarningsView(dataModel.getEarningsData(), PeriodUtils.getDivider(dataModel.getPeriod(), dataModel.getCustomPeriodInDays(), dataModel.getFromDate()));
            view.updateOperationChart(dataModel.getOperationsChartData());
            view.updateAverageTicketChart(dataModel.getAvgTicketChartData());
            view.updateTotalSalesChart(dataModel.getTotalSalesChartData());
            updatePeriodText();
        }
        updateOutletToolbarState();
        return cached;
    }

    @Override
    public void loadDataForSelectedPeriod() {
        updatePeriodText();
        refreshDataSubject.onNext(true);

    }

    private Observable createRequestObservable() {
        return Observable.mergeDelayError(
                Observable.fromCallable(() -> server.sendRequest(createEarningReportRequest(), EarningsReportResponse.class)).subscribeOn(Schedulers.computation()),
                Observable.fromCallable(() -> server.sendRequest(createWaresPeriodReportRequest(), WaresPeriodReportResponse.class)).subscribeOn(Schedulers.computation()),
                Observable.fromCallable(() -> server.sendRequest(createMerchantsReportRequest(), MerchantsReportResponse.class)).subscribeOn(Schedulers.computation()),
                Observable.fromCallable(() -> server.sendRequest(createCategoriesReportRequest(), CategoriesReportResponse.class)).subscribeOn(Schedulers.computation())
        );
    }

    private EarningsReportRequest createEarningReportRequest() {
        return new EarningsReportRequest(
                dataModel.getOwnerId(),
                dataModel.getCookieHash(),
                dataModel.getMerchantId(),
                PeriodUtils.getDivider(dataModel.getPeriod(), dataModel.getCustomPeriodInDays(), dataModel.getFromDate()),
                dataModel.getFromDate(),
                dataModel.getToDate(),
                PeriodUtils.getCompareBy(dataModel.getPeriod()),
                dataModel.getSelectedOutletIds(),dataModel.getStartTime(),
                dataModel.getEndTime());

    }

    private WaresPeriodReportRequest createWaresPeriodReportRequest() {
        return new WaresPeriodReportRequest(
                dataModel.getOwnerId(),
                dataModel.getMerchantId(),
                0, //offset
                dataModel.getFromDate(),
                dataModel.getToDate(),
                DataModel.SortBy.NET,
                DataModel.SortType.DESC,
                dataModel.getCookieHash(),
                dataModel.getSelectedOutletIds(),dataModel.getStartTime(),
                dataModel.getEndTime());
    }

    private MerchantsReportRequest createMerchantsReportRequest() {
        return new MerchantsReportRequest(
                dataModel.getOwnerId(),
                dataModel.getMerchantId(),
                0, //offset
                dataModel.getFromDate(),
                dataModel.getToDate(),
                DataModel.SortBy.NET,
                DataModel.SortType.DESC,
                dataModel.getCookieHash(),
                dataModel.getSelectedOutletIds(),dataModel.getStartTime(),
                dataModel.getEndTime());
    }

    private CategoriesReportRequest createCategoriesReportRequest() {
        return new CategoriesReportRequest(
                dataModel.getOwnerId(),
                dataModel.getMerchantId(),
                0, //offset
                dataModel.getFromDate(),
                dataModel.getToDate(),
                DataModel.SortBy.NET,
                DataModel.SortType.DESC,
                dataModel.getCookieHash(),
                dataModel.getSelectedOutletIds(),dataModel.getStartTime(),
                dataModel.getEndTime());
    }

    @Override
    public void nextPeriod() {
        super.nextPeriod();
        //TODO: fix reload data in any case
        if (view != null) {
            view.clearLists();
            loadDataForSelectedPeriod();
        }
    }

    @Override
    public void prevPeriod() {
        super.prevPeriod();
        if (view != null) {
            view.clearLists();
            loadDataForSelectedPeriod();
        }
    }

    @Override
    public void customPeriodUnitSelected(List<Date> dates,Integer startTime,Integer endTime) {
        super.customPeriodUnitSelected(dates,startTime,endTime);
        loadDataForSelectedPeriod();
    }

    @Override
    public void nonCustomPeriodUnitSelected(int period) {
        super.nonCustomPeriodUnitSelected(period);
        loadDataForSelectedPeriod();
    }

}
