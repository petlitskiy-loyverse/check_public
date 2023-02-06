package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

public abstract class BillingPresenter<T extends BaseView> extends BasePresenter<T> {

   protected BillingPresenter(DataModel dataModel, Server server) {
      super( dataModel, server );
   }

   public abstract void onBillingButtonClick();

   public abstract void onExitButtonClick();

   public abstract void onLogout();

}
