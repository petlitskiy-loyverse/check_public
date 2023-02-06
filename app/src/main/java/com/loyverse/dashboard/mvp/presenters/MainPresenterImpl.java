package com.loyverse.dashboard.mvp.presenters;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.loyverse.dashboard.base.LogoutEvent;
import com.loyverse.dashboard.base.PermissionEvent;
import com.loyverse.dashboard.base.firebase.StockPushService;
import com.loyverse.dashboard.base.multishop.Permission;
import com.loyverse.dashboard.base.mvp.MainPresenter;
import com.loyverse.dashboard.base.mvp.MainView;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.AccountPermissionsRequest;
import com.loyverse.dashboard.core.api.AccountPermissionsResponse;
import com.loyverse.dashboard.core.api.GetOwnerProfileRequest;
import com.loyverse.dashboard.core.api.GetOwnerProfileResponse;
import com.loyverse.dashboard.core.api.LogOutOwnerRequest;
import com.loyverse.dashboard.core.api.LogOutOwnerResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainPresenterImpl extends MainPresenter<MainView> {
    private boolean loadingPermissions = false;
    @TabType
    private int currentTab = EMPTY_TAB;
    private int outletIdFromPush = 0;
    private HashSet<String> logoutResult = new HashSet<>(Arrays.asList(
            ServerResult.BAD_COOKIE_AUTH.result,
            ServerResult.ACCOUNT_VOIDED.result,
            ServerResult.DEVICE_UNASSIGNED.result
    ));
    private Action1<Throwable> onPreloadError = throwable -> {
        view.hideLoadingDialog();
        if (throwable instanceof ServerError) {
            if (logoutResult.contains(((ServerError) throwable).getResult())) {
                dataModel.deleteLoginData();
                view.showLoginScreen();
                return;
            }
        }
        if (throwable instanceof IOException) {
            view.showNoInternetRetryDialog();
        } else view.showRetryDialog();
    };
    private Action1<LogOutOwnerResponse> onLogout = response -> {
        view.hideLoadingDialog();
        logout();
    };
    private final Action1<GetOwnerProfileResponse> onProfileData = response -> view.setProfileData(response);

    public MainPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    public boolean isPermissionsPreloaded() {
        return dataModel.getPermissions().size() > 0;
    }

    private AccountPermissionsRequest createRequest() {
        return new AccountPermissionsRequest(
                dataModel.getOwnerId(),
                dataModel.getMerchantId(),
                dataModel.getCookieHash()
        );
    }

    @Override
    public void bind(MainView view) {
        super.bind(view);
        FirebaseCrashlytics.getInstance().setUserId(String.valueOf(dataModel.getMerchantId()));
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        if (dataModel.getPermissions().size() == 0)
            updatePermission();
        else {
            updateTabs();
        }
    }

    @SuppressWarnings("ResourceType")
    private void updateTabs() {
        List<Integer> tabByPermissions = getTabByPermissions(dataModel.getPermissions());
        if (currentTab == EMPTY_TAB) {
            if (tabByPermissions.size() != 0) {
                currentTab = tabByPermissions.get(0);
            }
        } else if (!tabByPermissions.contains(currentTab))
            tabByPermissions.add(currentTab);

        Collections.sort(tabByPermissions);
        @TabType int[] tabs = wrapTabListAsPrimitive(tabByPermissions);

        view.setTabs(tabs);
        view.showTab(currentTab);
    }

    private void updatePermission() {
        loadingPermissions = true;

        if (dataModel.getPermissions().size() == 0)
            view.hideBottomBar(false);
        view.showLoadingDialog();
        subscriptions.add(Observable.fromCallable(() -> server.sendRequest(createRequest(), AccountPermissionsResponse.class))
                .subscribeOn(Schedulers.io())
                .filter(accountPermissionsResponse -> accountPermissionsResponse.permissions != null)
                .observeOn(AndroidSchedulers.mainThread())
                .map(accountPermissionsResponse -> {
                    List<String> permissions = Arrays.asList(accountPermissionsResponse.permissions);
                    dataModel.updatePermissions(permissions);

                    return getTabByPermissions(dataModel.getPermissions());
                })
                .subscribe(tabByPermissions -> {
                    updateTabs();
                    view.hideLoadingDialog();
                    //if outletIdFromPush != 0 then notification must be handled
                    if (outletIdFromPush != 0) {
                        showPushNotification(outletIdFromPush);
                    }
                    loadingPermissions = false;
                }, onPreloadError));
    }

    @Override
    public void getOwnerProfile() {
        subscriptions.add(Observable.fromCallable(() ->
                server.sendRequest(createOwnerProfileRequest(), GetOwnerProfileResponse.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onProfileData, onError));
    }

    private GetOwnerProfileRequest createOwnerProfileRequest() {
        return new GetOwnerProfileRequest(
                dataModel.getCookieHash(),
                dataModel.getOwnerId(),
                dataModel.getMerchantId(),
                view.getDeviceId());
    }

    @SuppressWarnings("ResourceType")
    private
    @TabType
    int[] wrapTabListAsPrimitive(List<Integer> integerList) {
        @TabType int[] ints = new int[integerList.size()];
        for (int i = 0; i < integerList.size(); i++) {
            ints[i] = integerList.get(i);
        }
        return ints;
    }

    private List<Integer> getTabByPermissions(List<String> permissions) {
        List<Integer> tabList = new ArrayList<>();
        if (permissions.contains(Permission.ACCESS_REPORTS))
            tabList.add(REPORTS_TAB);

        if (permissions.contains(Permission.ACCESS_WARES))
            tabList.add(PRODUCTS_TAB);

        tabList.add(SETTINGS_TAB);
        return tabList;
    }

    @Override
    public void unbind(MainView view) {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.unbind(view);
    }

    @Subscribe(sticky = true)
    public void onPermissionEvent(PermissionEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

        if (event.getType() == PermissionEvent.ACCESS_DENIED && !loadingPermissions) {
            updatePermission();
        }
        if (event.getType() == PermissionEvent.BLOCKED_BY_BILLING) {
            view.showBillingLockScreen();
        }
    }


    /**
     * Ad-hoc solution for resetting current tab
     *
     * @param event event
     */
    @Subscribe(sticky = true)
    public void onLogoutEvent(LogoutEvent event) {
        if (event.isForce()) {
            logout();
        } else {
            if (view != null) {
                view.showLoadingDialog();
                subscriptions.add(server.request(new LogOutOwnerRequest(
                                dataModel.getCookieHash(),
                                dataModel.getOwnerId(),
                                dataModel.getMerchantId(),
                                view.getDeviceId()),
                        LogOutOwnerResponse.class, onLogout, onError));
            }
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    private void logout() {
        dataModel.deleteLoginData();
        currentTab = EMPTY_TAB;
        view.showLoginScreen();
    }

    @Override
    public boolean isLoggedIn() {
        return dataModel.isUserLoggedIn();
    }

    @Override
    public void onTabClick(@TabType int tab) {
        if (view != null) {
            List<Integer> tabByPermission = getTabByPermissions(dataModel.getPermissions());
            if (!tabByPermission.contains(currentTab)) {
                view.setTabs(wrapTabListAsPrimitive(tabByPermission));
            }

            currentTab = tab;
            view.showTab(tab);
        }
    }

    @Override
    public void onPushReceived(int outletId) {
        if (loadingPermissions)
            outletIdFromPush = outletId;
        else {
            outletIdFromPush = 0;
            showPushNotification(outletId);
        }
    }

    @Override
    public void onRetryClick() {
        updatePermission();
    }

    private void showPushNotification(int outletId) {
        if (currentTab != PRODUCTS_TAB) {
            currentTab = PRODUCTS_TAB;
            view.showTab(PRODUCTS_TAB);
            EventBus.getDefault().postSticky(new StockPushService.StockChangedEvent(false, outletId));
        } else
            EventBus.getDefault().postSticky(new StockPushService.StockChangedEvent(true, outletId));
    }

    @Subscribe(sticky = true)
    public void onEvent(StockPushService.StockChangedEvent event) {
        if (event.refreshData) dataModel.clearProductList();
    }

}
