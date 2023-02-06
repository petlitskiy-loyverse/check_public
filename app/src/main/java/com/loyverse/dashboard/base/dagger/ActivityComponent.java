package com.loyverse.dashboard.base.dagger;

import com.loyverse.dashboard.base.BaseActivity;
import com.loyverse.dashboard.base.dagger.module.BillingPresenterModule;
import com.loyverse.dashboard.base.dagger.module.LoginPresenterModule;
import com.loyverse.dashboard.base.dagger.module.MainPresenterModule;
import com.loyverse.dashboard.base.dagger.module.OutletsPresenterModule;
import com.loyverse.dashboard.base.dagger.module.ProductsPresenterModule;
import com.loyverse.dashboard.base.dagger.module.ReportsPresenterModule;
import com.loyverse.dashboard.base.dagger.module.ResetPasswordPresenterModule;
import com.loyverse.dashboard.base.dagger.module.SalesPresenterModule;
import com.loyverse.dashboard.base.dagger.module.SalesSummaryPresenterModule;
import com.loyverse.dashboard.base.dagger.module.SettingsPresenterModule;
import com.loyverse.dashboard.mvp.views.BillingActivity;
import com.loyverse.dashboard.mvp.views.LoginActivity;
import com.loyverse.dashboard.mvp.views.MainActivity;
import com.loyverse.dashboard.mvp.views.OutletsFragment;
import com.loyverse.dashboard.mvp.views.PosInformationActivity;
import com.loyverse.dashboard.mvp.views.ProductsFragment;
import com.loyverse.dashboard.mvp.views.ReportsFragment;
import com.loyverse.dashboard.mvp.views.ResetPasswordActivity;
import com.loyverse.dashboard.mvp.views.SalesFragment;
import com.loyverse.dashboard.mvp.views.SalesSummaryFragment;
import com.loyverse.dashboard.mvp.views.SettingsFragment;
import com.loyverse.dashboard.mvp.views.dialogs.CalendarDialogFragment;

import dagger.Component;

@ActivityScope
@Component(modules = {
        LoginPresenterModule.class,
        ReportsPresenterModule.class,
        SalesPresenterModule.class,
        SalesSummaryPresenterModule.class,
        ResetPasswordPresenterModule.class,
        ProductsPresenterModule.class,
        MainPresenterModule.class,
        SettingsPresenterModule.class,
        OutletsPresenterModule.class,
        BillingPresenterModule.class

}, dependencies = ApplicationComponent.class)
public interface ActivityComponent {

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(ReportsFragment activity);

    void inject(SalesFragment activity);

    void inject(SalesSummaryFragment activity);

    void inject(ResetPasswordActivity activity);

    void inject(BaseActivity activity);

    void inject(ProductsFragment activity);

    void inject(SettingsFragment activity);

    void inject(OutletsFragment activity);

    void inject(BillingActivity activity);

    void inject(PosInformationActivity activity);

    void inject(CalendarDialogFragment activity);

}