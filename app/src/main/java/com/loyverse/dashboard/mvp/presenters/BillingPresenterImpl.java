package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.multishop.Permission;
import com.loyverse.dashboard.base.mvp.BillingPresenter;
import com.loyverse.dashboard.base.mvp.BillingView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public class BillingPresenterImpl extends BillingPresenter<BillingView> {

    public BillingPresenterImpl(DataModel dataModel, Server server) {
        super( dataModel, server );
    }

    @Override
    public void bind(BillingView view) {
        super.bind( view );
        if (dataModel.getPermissions().contains( Permission.ACCESS_BILLING )) {
            view.showButtonBilling();
        } else {
            view.hideButtonBilling();
        }
    }

    @Override
    public void onBillingButtonClick() {
        view.goToBillingView();
    }

    @Override
    public void onExitButtonClick() {
        dataModel.deleteLoginData();
        view.exitFromBilling();
    }

    @Override
    public void onLogout() {
        if (view != null)
            view.showBillingExitDialog();
    }


}
