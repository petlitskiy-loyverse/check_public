package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.FillSelectData;
import com.loyverse.dashboard.core.api.Outlet;
import com.loyverse.dashboard.core.api.OutletListRequest;
import com.loyverse.dashboard.core.api.OutletListResponse;

import java.util.Arrays;
import java.util.HashSet;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//special presenter for hell work
public abstract class OutletPreloadPresenter<T extends OutletsPreloadView> extends BasePresenter<T> {

    OutletPreloadPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    protected boolean isOutletPreloaded() {
        return dataModel.getOutletList().size() > 0;
    }

    protected Observable<OutletListResponse> createOutletsPreloadObservable() {
        return Observable.fromCallable(() -> server.sendRequest(createRequest(), OutletListResponse.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(outletListResponse -> {
                    dataModel.updateOutletList(Arrays.asList(outletListResponse.outlets));
                    handleFillSelectDataResponse(outletListResponse.fillSelectData);
                    return outletListResponse;
                })
                .map(outletListResponse -> {
                    // TODO: 01.02.17 Throw error if outletListResponse.outlets.length == 0
                    if (outletListResponse.outlets.length == 0)
                        view.hideLoadingDialog();

                    return outletListResponse;
                })
                .observeOn(Schedulers.io())
                .filter(outletListResponse -> outletListResponse.outlets.length > 0);
    }

    private OutletListRequest createRequest() {
        return new OutletListRequest(
                dataModel.getOwnerId(),
                dataModel.getCookieHash(),
                dataModel.getMerchantId(),
                dataModel.getFromDate(),
                dataModel.getToDate());
    }

    protected void updateOutletToolbarState() {
        HashSet<Outlet> outlets = new HashSet<>(dataModel.getOutletList());
        outlets.addAll(dataModel.getPeriodOutletList());
        if (outlets.size() > 1) {
            view.showOutletTitleAndOutletsIcon();
            view.setOutletName(dataModel.getOutletName()); // TODO: 27.12.16 Possible problem with updating
        } else {
            view.hideOutletTitleAndOutletsIcon();
        }
    }

    public void handleFillSelectDataResponse(FillSelectData response) {
        if (response.result.equals(Utils.SERVER_RESUL_OK))
            dataModel.updatePeriodOutletList(Arrays.asList(response.outlets));

        updateOutletToolbarState();
    }
}
