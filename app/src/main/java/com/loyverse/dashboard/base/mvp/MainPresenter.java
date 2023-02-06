package com.loyverse.dashboard.base.mvp;

import androidx.annotation.IntDef;

import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class MainPresenter<T extends MainView> extends BasePresenter<T> {
    public static final int EMPTY_TAB = -1;
    public static final int REPORTS_TAB = 0;
    public static final int PRODUCTS_TAB = 1;
    public static final int SETTINGS_TAB = 2;

    protected MainPresenter(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public abstract boolean isLoggedIn();

    public abstract void onTabClick(@TabType int tab);

    public abstract void onPushReceived(int outletId);

    public abstract void onRetryClick();

    public abstract void getOwnerProfile();

    @IntDef({EMPTY_TAB, REPORTS_TAB, PRODUCTS_TAB, SETTINGS_TAB})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabType {

    }
}
