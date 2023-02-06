package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.OutletsPresenter;
import com.loyverse.dashboard.base.mvp.OutletsView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.Outlet;
import com.loyverse.dashboard.core.api.OutletListRequest;
import com.loyverse.dashboard.core.api.OutletListResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.functions.Action1;

public class OutletsPresenterImpl extends OutletsPresenter<OutletsView> {
    private boolean includedPeriodOutlets = false;

    private Action1<OutletListResponse> onNext = response -> {
        dataModel.updateOutletList(Arrays.asList(response.outlets));

        if (response.fillSelectData.result.equals(Utils.SERVER_RESUL_OK))
            dataModel.updatePeriodOutletList(Arrays.asList(response.fillSelectData.outlets));
        else
            dataModel.updatePeriodOutletList(new ArrayList<>());

        List<Outlet> outletList = composeOutletList();
        view.updateData(outletList, dataModel.getSelectedOutlets());
    };

    public OutletsPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    private List<Outlet> composeOutletList() {
        List<Outlet> outletList = new ArrayList(dataModel.getOutletList());
        if (includedPeriodOutlets) {
            final List<Outlet> periodOutlets = dataModel.getPeriodOutletList();
            for (Outlet outlet : periodOutlets) {
                if (!outletList.contains(outlet))
                    outletList.add(outlet);
            }
        }
        Collections.sort(outletList, (outlet, t1) -> outlet.name.compareToIgnoreCase(t1.name));
        return outletList;
    }

    @Override
    public void bind(OutletsView view) {
        super.bind(view);
        includedPeriodOutlets = view.isIncludedPeriodOutlets();
        if (!provideDataFromModel())
            loadData();
    }

    private boolean provideDataFromModel() {
        boolean isSet = false;
        if (dataModel.getOutletList().size() > 0 && dataModel.getSelectedOutlets().size() > 0) {
            isSet = true;
            view.updateData(composeOutletList(), dataModel.getSelectedOutlets());
        }
        return isSet;
    }

    @Override
    public void onOutletSelected(Outlet outlet) {
        List<Outlet> list = new ArrayList<>();
        list.add(outlet);
        dataModel.setSelectedOutlets(list);
        view.updateData(dataModel.getOutletList(), dataModel.getSelectedOutlets());
        view.close();
    }

    @Override
    public void loadData() {
        view.showLoadingDialog();
        view.cleanData();

        subscriptions.add(server.request(
                new OutletListRequest(
                        dataModel.getOwnerId(),
                        dataModel.getCookieHash(),
                        dataModel.getMerchantId(),
                        dataModel.getFromDate(),
                        dataModel.getToDate()),
                OutletListResponse.class,
                onNext, onError));
    }

    public boolean isIncludedPeriodOutlets() {
        return includedPeriodOutlets;
    }

    public void setIncludedPeriodOutlets(boolean includedPeriodOutlets) {
        this.includedPeriodOutlets = includedPeriodOutlets;
    }
}
