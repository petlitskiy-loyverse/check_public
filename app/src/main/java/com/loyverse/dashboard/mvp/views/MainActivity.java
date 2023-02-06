package com.loyverse.dashboard.mvp.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseActivity;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.firebase.StockJobIntentService;
import com.loyverse.dashboard.base.mvp.MainPresenter;
import com.loyverse.dashboard.base.mvp.MainView;
import com.loyverse.dashboard.base.mvp.OutletsView;
import com.loyverse.dashboard.base.mvp.ScrolledView;
import com.loyverse.dashboard.core.api.GetOwnerProfileResponse;
import com.loyverse.dashboard.mvp.presenters.MainPresenterImpl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity implements MainView {
    private final static String LOG_CONTEXT = "MainActivity";

    @BindView(R.id.bottombar)
    protected AHBottomNavigation bottomBar;

    @Inject
    MainPresenter<MainView> presenter;

    AHBottomNavigationItem reportsTab;
    AHBottomNavigationItem productsTab;
    AHBottomNavigationItem settingsTab;

    private List<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private boolean showProducts = false;
    private boolean initialized = false;

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addDebugDrawer();
        ButterKnife.bind(this);
        ((BaseApplication) getApplication()).getActivityComponent().inject(this);
        setUpBottomBar();
    }

    @Override
    protected void onStart() {
        presenter.bind(this);
        if (getIntent().getAction() != null) {
            showProducts = true;
            getIntent().setAction(null);
        }

        if (presenter.isLoggedIn())
            presenter.getOwnerProfile();

        super.onStart();
    }

    @Override
    protected void onStop() {
        presenter.unbind(this);

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initialized = true;
        // TODO: 03.02.17 Move logic to the presenter
        if (showProducts) {
            Timber.i("low_stock_notification_opened");
            if (!presenter.isLoggedIn())
                navigator.showLoginScreen(this);
            else {
                int outletId = getIntent().getIntExtra(StockJobIntentService.OUTLET_ID, 0);
                presenter.onPushReceived(outletId);
            }
            showProducts = false;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "onNewIntent", intent.getAction() != null ? intent.getAction() : ""));
        if (intent.getAction() != null) {
            showProducts = true;
            getIntent().setAction(null);
            getIntent().putExtra(StockJobIntentService.OUTLET_ID, intent.getIntExtra(StockJobIntentService.OUTLET_ID, 0));
        }
        super.onNewIntent(intent);
    }

    protected void setUpBottomBar() {
        float activeTitleSize = getResources().getDimension(R.dimen.bottombar_title_size_active);
        float inactiveTitleSize = getResources().getDimension(R.dimen.bottombar_title_size_inactive);
        int activeColor = getResources().getColor(R.color.bottom_bar_active_color);
        int inactiveColor = getResources().getColor(R.color.bottom_bar_inactive_color);

        bottomBar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        reportsTab = new AHBottomNavigationItem(R.string.tab_reports, R.drawable.ic_home, R.color.tab_color);
        productsTab = new AHBottomNavigationItem(R.string.tab_products, R.drawable.ic_list, R.color.tab_color);
        settingsTab = new AHBottomNavigationItem(R.string.tab_settings, R.drawable.ic_settings, R.color.tab_color);

        bottomBar.setTitleTextSize(activeTitleSize, inactiveTitleSize);
        bottomBar.setColoredModeColors(activeColor, inactiveColor);
        bottomBar.setBehaviorTranslationEnabled(false);
        bottomBar.setUseElevation(true);
        bottomBar.setForceTint(true);
        bottomBar.setColored(true);
        bottomBar.setOnTabSelectedListener((position, wasSelected) -> {
            @MainPresenter.TabType int tab = getTabTypeByIndex(position);
            switch (tab) {
                case MainPresenter.REPORTS_TAB:
                    Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Report tab click"));
                    break;
                case MainPresenter.PRODUCTS_TAB:
                    Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Products tab click"));
                    break;
                case MainPresenter.SETTINGS_TAB:
                    Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Settings tab click"));
                    break;
            }

            if (initialized)
                presenter.onTabClick(tab);

            return false;
        });
    }

    @Override
    public void onBackPressed() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Back button click"));
        Fragment currentFragment = navigator.getCurrentFragment(this);

        if (currentFragment instanceof OutletsView) {
            super.onBackPressed();
        } else if (bottomBar.getCurrentItem() == 0) {
            if (currentFragment instanceof SettingsFragment)
                finish();
            else if (currentFragment instanceof ProductsFragment) {
                if (((ProductsFragment) currentFragment).onBackPressed())
                    bottomBar.setCurrentItem(0);
                else finish();
            } else if (currentFragment instanceof ReportsFragment) {
                finish();
            } else bottomBar.setCurrentItem(0);
        } else if (currentFragment instanceof ProductsFragment) {
            if (((ProductsFragment) currentFragment).onBackPressed())
                bottomBar.setCurrentItem(0);
        } else {
            bottomBar.setCurrentItem(0);
        }
    }

    @Override
    public void hideBottomBar(boolean animated) {
        if (!bottomBar.isHidden())
            bottomBar.hideBottomNavigation(animated);
    }

    @Override
    public void showBottomBar(boolean animated) {
        if (bottomBar.isHidden())
            bottomBar.restoreBottomNavigation(animated);
    }

    private void updateBottomNavTabs() {
        bottomBar.removeAllItems();
        bottomBar.addItems(bottomNavigationItems);
    }

    @Override
    public void setTabs(@MainPresenter.TabType int[] tabs) {
        bottomNavigationItems.clear();
        for (int tabType : tabs) {
            if (tabType == MainPresenter.REPORTS_TAB)
                bottomNavigationItems.add(reportsTab);
            else if (tabType == MainPresenterImpl.PRODUCTS_TAB)
                bottomNavigationItems.add(productsTab);
            else bottomNavigationItems.add(settingsTab);
        }
        if (bottomNavigationItems.size() <= 1)
            hideBottomBar(true);

        updateBottomNavTabs();
    }

    @Override
    public void showTab(@MainPresenter.TabType int tab) {
        int index = -1;
        Fragment currentFragment = navigator.getCurrentFragment(this);
        switch (tab) {
            case MainPresenter.REPORTS_TAB:
                showReportsTab(currentFragment);
                index = bottomNavigationItems.indexOf(reportsTab);
                break;
            case MainPresenter.PRODUCTS_TAB:
                if (currentFragment != null && currentFragment.getTag().equals(ProductsFragment.TAG))
                    ((ScrolledView) currentFragment).scrollToTheTop();
                else
                    navigator.showProductsFragment(this);
                index = bottomNavigationItems.indexOf(productsTab);
                break;
            case MainPresenter.SETTINGS_TAB:
                Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Settings tab click"));
                if (currentFragment == null || !currentFragment.getTag().equals(SettingsFragment.TAG))
                    navigator.showSettingsFragment(this);
                index = bottomNavigationItems.indexOf(settingsTab);
        }

        if (index != -1) {
            bottomBar.setCurrentItem(index, false);
        }
    }

    private void showReportsTab(Fragment currentFragment) {
        if (currentFragment != null) {
            final String tag = currentFragment.getTag();
            if (!initialized && (tag.equals(SalesFragment.TAG) || tag.equals(SalesSummaryFragment.TAG))) {
                return;
            } else if (tag.equals(ReportsFragment.TAG)) {
                ((ScrolledView) currentFragment).scrollToTheTop();
            } else navigator.showMainReportsFragment(this);
        } else navigator.showMainReportsFragment(this);
    }

    @Override
    public void showLoginScreen() {
        navigator.showLoginScreen(this);
    }

    @Override
    public void showBillingLockScreen() {
        navigator.showBillingLockScreen(this);
    }

    @Override
    public void showRetryDialog() {
        navigator.showRetryDialog(this);
    }

    @Override
    public void showNoInternetRetryDialog() {
        navigator.showNoInternetRetryDialog(this);
    }

    @Override
    public void setProfileData(GetOwnerProfileResponse response) {
        Utils.saveToSharedPreferences(getApplicationContext(), response.toJSON(), Utils.MONEY_FORMAT_KEY);
    }

    @Override
    public void onRetryClick() {
        presenter.onRetryClick();
    }

    @Override
    public int getCurrentTab() {
        return getTabTypeByIndex(bottomBar.getCurrentItem());
    }

    @Override
    public String getDeviceId() {
        return Utils.getDeviceId(this);
    }

    @MainPresenter.TabType
    private int getTabTypeByIndex(int index) {
        if (bottomNavigationItems.size() == 0)
            return MainPresenter.EMPTY_TAB;

        AHBottomNavigationItem currentTab = bottomNavigationItems.get(index);
        if (currentTab.equals(reportsTab)) {
            return MainPresenter.REPORTS_TAB;
        }
        if (currentTab.equals(productsTab)) {
            return MainPresenter.PRODUCTS_TAB;
        } else if (currentTab.equals(settingsTab))
            return MainPresenter.SETTINGS_TAB;
        return MainPresenter.EMPTY_TAB;
    }
}