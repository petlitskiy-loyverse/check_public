package com.loyverse.dashboard.core;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.mvp.views.WebActivity;
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
import com.loyverse.dashboard.mvp.views.dialogs.AlertDialogFragment;
import com.loyverse.dashboard.mvp.views.dialogs.BillingOutDialog;
import com.loyverse.dashboard.mvp.views.dialogs.CalendarDialogFragment;
import com.loyverse.dashboard.mvp.views.dialogs.EmailSentDialogFragment;
import com.loyverse.dashboard.mvp.views.dialogs.LogoutDialogFragment;
import com.loyverse.dashboard.mvp.views.dialogs.RetryDialog;
import com.loyverse.dashboard.mvp.views.dialogs.SelectPeriodDialogFragment;

import javax.inject.Inject;

import timber.log.Timber;

public class Navigator {

    public static final String TITLE_KEY = "title";
    public static final String EMAIL_KEY = "email";
    public static final String URL_KEY = "url";
    public static final int CONTENT_LAYOUT = R.id.content_layout;
    private static final String POS_PACKAGE = "market://details?id=com.loyverse.sale";
    private static final String POS_WEBSITE = "https://loyverse.com/";
    public static final String PRIVACY_LINK = "https://loyverse.com/privacy-policy?utm_source=Loyverse_Dashboard&utm_medium=Privacy_policy";
    public static final String TERMS_LINK =   "https://loyverse.com/terms-use?utm_source=Loyverse_Dashboard&utm_medium=Terms_of_use";

    @Inject
    public Navigator() {
        Timber.d( "constructor" );
    }

    public void showMainScreen(FragmentActivity activity) {
        Intent intent = new Intent( activity, MainActivity.class );
        activity.startActivity( intent );
        activity.finish();
    }

    public void showLoginScreen(FragmentActivity activity) {
        Timber.i( "sign_out" );
        Intent intent = new Intent( activity, LoginActivity.class );
        activity.startActivity( intent );
        activity.finish();
    }

    public void showBillingLockScreen(FragmentActivity activity) {
        Intent intent = new Intent( activity, BillingActivity.class );
        activity.startActivity( intent );
        activity.finish();
    }

    public void showResetPasswordScreen(FragmentActivity activity, String email) {
        Intent intent = new Intent( activity, ResetPasswordActivity.class );
        intent.putExtra( EMAIL_KEY, email );
        activity.startActivity( intent );
    }

    public Fragment getCurrentFragment(FragmentActivity activity) {
        return activity.getSupportFragmentManager().findFragmentById( CONTENT_LAYOUT );
    }

    public void showMainReportsFragment(FragmentActivity activity) {
        Timber.i( "sales_reports_screen" );
        if (!popFromBackStack( activity, ReportsFragment.TAG ))
            addToBackStack( activity, new ReportsFragment(), ReportsFragment.TAG );
    }

    public void showOutletsFragment(FragmentActivity activity, boolean includePeriodOutlets) {
        Timber.i( "select_store_screen" );
        addToBackStack( activity, OutletsFragment.newInstance( includePeriodOutlets ), OutletsFragment.TAG );
    }

    public void showSalesFragment(FragmentActivity activity, String key) {
        Timber.i( String.format( "sales_by_%s_screen", key ) );
        if (!popFromBackStack( activity, SalesFragment.TAG ))
            addToBackStack( activity, SalesFragment.newInstance( key ), SalesFragment.TAG );
    }

    public void showSalesSummaryFragment(FragmentActivity activity) {
        Timber.i( "sales_summary_screen" );
        if (!popFromBackStack( activity, SalesSummaryFragment.TAG ))
            addToBackStack( activity, new SalesSummaryFragment(), SalesSummaryFragment.TAG );
    }

    public void showSettingsFragment(FragmentActivity activity) {
        Timber.i( "settings_screen" );
        if (!popFromBackStack( activity, SettingsFragment.TAG ))
            addToBackStack( activity, new SettingsFragment(), SettingsFragment.TAG );

    }

    public void showProductsFragment(FragmentActivity activity) {
        Timber.i( "items_screen" );
        if (!popFromBackStack( activity, ProductsFragment.TAG ))
            addToBackStack( activity, new ProductsFragment(), ProductsFragment.TAG );
    }

    public void showPosInPlayStore(FragmentActivity activity) {
        Timber.i( "download_loyverse_pos_button" );
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( POS_PACKAGE ) );
        if (intent.resolveActivity( activity.getPackageManager() ) == null)
            intent.setData( Uri.parse( POS_WEBSITE ) );
        activity.startActivity( intent );
    }

    public void showPosInformationScreen(FragmentActivity activity) {
        Timber.i("new_to_loyverse_screen");
        Intent intent = new Intent(activity, PosInformationActivity.class);
        activity.startActivity(intent);
    }

    public void showSelectPeriodDialog(int currentPeriod, FragmentActivity activity) {
        showDialog( activity, SelectPeriodDialogFragment.newInstance( currentPeriod ), SelectPeriodDialogFragment.TAG );

    }

    public void showRetryDialog(FragmentActivity activity) {
        showDialog( activity, RetryDialog.newInstance(R.string.retry_dialog_msg), RetryDialog.TAG );
    }

    public void showNoInternetRetryDialog(FragmentActivity activity) {
        showDialog( activity, RetryDialog.newInstance(R.string.retry_dialog_no_internet_msg), RetryDialog.TAG );
    }

    public void showCalendarDialog(FragmentActivity activity, String text, long from, long to,Integer startTime,Integer endTime) {
        showDialog( activity, CalendarDialogFragment.newInstance(from, to, text,startTime,endTime), CalendarDialogFragment.TAG );
    }

    public void showEmailNotExistDialog(FragmentActivity activity) {
        showDialog( activity, new AlertDialogFragment().setMsg( activity.getResources().getString( R.string.error_email_not_exist ) ), AlertDialogFragment.TAG );
    }

    public void showWrongEmailOrPasswordDialog(FragmentActivity activity) {
        showDialog( activity, new AlertDialogFragment().setMsg( activity.getResources().getString( R.string.error_wrong_password ) ), AlertDialogFragment.TAG );
    }

    public void showAccessRightsRequiredDialog(FragmentActivity activity) {
        showDialog( activity, new AlertDialogFragment().setMsg( activity.getResources().getString( R.string.access_rights_required_msg ) )
                .setTitle( activity.getResources().getString( R.string.access_rights_required ) ), AlertDialogFragment.TAG );
    }

    public void showEmailSentDialog(FragmentActivity activity) {
        showDialog( activity, new EmailSentDialogFragment(), EmailSentDialogFragment.TAG );
    }

    public void showLogoutDialog(FragmentActivity activity) {
        showDialog( activity, new LogoutDialogFragment(), LogoutDialogFragment.TAG );
    }

    public void showBillingExitDialog(FragmentActivity activity) {
        showDialog( activity, new BillingOutDialog(), BillingOutDialog.TAG );
    }

    /**
     * @return true if fragment was already in backstack, is popped and displayed,
     * if false - call addToBackStack method to create new Fragment
     */
    private boolean popFromBackStack(FragmentActivity activity, String tag) {
        try {
            return activity.getSupportFragmentManager().popBackStackImmediate( tag, 0 );
        } catch (IllegalStateException ignored) {
            return false;
        }
    }

    /**
     * displays and adds provided Fragment to backstack, ignores the IllegalStateException, todo temp solution
     */
    private void addToBackStack(FragmentActivity activity, Fragment fragment, String tag) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace( CONTENT_LAYOUT, fragment, tag )
                .addToBackStack( tag )
                .commitAllowingStateLoss();
    }

    /**
     * displays provided DialogFragment, ignores the IllegalStateException, todo temp solution
     */
    private void showDialog(FragmentActivity activity, AppCompatDialogFragment dialog, String tag) {
        activity.getSupportFragmentManager().beginTransaction()
                .add( dialog, tag )
                .commitAllowingStateLoss();
    }

    public void showAccessDeneidFragment(FragmentActivity activity) {
        Timber.i( "items_screen" );
        if (!popFromBackStack( activity, ProductsFragment.TAG ))
            addToBackStack( activity, new ProductsFragment(), ProductsFragment.TAG );
    }

    public void openWebpage(FragmentActivity activity, String url) {
        Intent intent = new Intent(activity, WebActivity.class);
        intent.putExtra(URL_KEY, url);
        activity.startActivity(intent);
    }

    public void openExternalBrowserFor(Activity activity, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        activity.startActivity(intent);
    }
}