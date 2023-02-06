package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.PeriodUtils;
import com.loyverse.dashboard.base.mvp.SalesSummaryPresenter;
import com.loyverse.dashboard.base.mvp.SalesSummaryView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.EarningsReportRequest;
import com.loyverse.dashboard.core.api.EarningsReportResponse;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class SalesSummaryPresenterImpl extends SalesSummaryPresenter<SalesSummaryView> {

    private PublishSubject<Boolean> refreshDataSubject = PublishSubject.create();
    private Action1<EarningsReportResponse> onNext = response -> {
        handleFillSelectDataResponse(response.fillSelectData);

        updatePeriodText();
        dataModel.setEarningTotalValues(response.totalValues);
        dataModel.setHideFields(response.hideFields);
        view.updateData(
            response.totalValues,
            response.hideFields
        );
    };

    public SalesSummaryPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void bind(SalesSummaryView view) {
        super.bind(view);
        Subscription subscription = refreshDataSubject.debounce(DEBOUNCE_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        event -> {
                            this.view.showLoadingDialog();
                            Observable observable;
                            if (!isOutletPreloaded()) {
                                observable = createOutletsPreloadObservable()
                                        .flatMap(outletListResponse -> createEarningReportRequestObservable());
                            } else {
                                observable = createEarningReportRequestObservable()
                                        .subscribeOn(Schedulers.io());
                            }

                            observable = observable.observeOn(AndroidSchedulers.mainThread());
                            subscriptions.add(observable.subscribe(onNext, onError));
                        },
                        Timber::e
                );
        subscriptions.add(subscription);

        updatePeriodText();
        if (!provideDataFromModel()) loadData();
    }

    private boolean provideDataFromModel() {
        updateOutletToolbarState();
        if (dataModel.getEarningTotalValues() != null) {
            view.updateData(
                dataModel.getEarningTotalValues(),
                dataModel.getHideFields()
            );
            return true;
        }
        return false;
    }

    @Override
    public void loadData() {
        updatePeriodText();
        refreshDataSubject.onNext(true);
    }

    private Observable<EarningsReportResponse> createEarningReportRequestObservable() {
        return Observable.fromCallable(() -> server.sendRequest(
                new EarningsReportRequest(
                        dataModel.getOwnerId(),
                        dataModel.getCookieHash(),
                        dataModel.getMerchantId(),
                        PeriodUtils.getDivider(
                                dataModel.getPeriod(),
                                dataModel.getCustomPeriodInDays(),
                                dataModel.getFromDate()
                        ),
                        dataModel.getFromDate(),
                        dataModel.getToDate(),
                        PeriodUtils.getCompareBy(dataModel.getPeriod()),
                        dataModel.getSelectedOutletIds(), dataModel.getStartTime(), dataModel.getEndTime()),
                EarningsReportResponse.class
        ));
    }

    @Override
    public void prevPeriod() {
        super.prevPeriod();
        loadData();
    }

    @Override
    public void nextPeriod() {
        super.nextPeriod();
        loadData();
    }

    @Override
    public void nonCustomPeriodUnitSelected(int period) {
        super.nonCustomPeriodUnitSelected(period);
        loadData();
    }

    @Override
    public void customPeriodUnitSelected(List<Date> dates,Integer startTime,Integer endTime) {
        super.customPeriodUnitSelected(dates,startTime,endTime);
        loadData();
    }
}
