package com.loyverse.dashboard.mvp.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseActivity;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.BillingPresenter;
import com.loyverse.dashboard.base.mvp.BillingView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;

public class BillingActivity extends BaseActivity implements BillingView {

    public static final String TAG = "SettingsFragment";

    Button button;
    Button ButtonExit;
    TextView textView1;
    TextView textView2;

    @Inject
    BillingPresenter<BillingView> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.layout_deny_acces );
        addDebugDrawer();
        ButterKnife.bind( this );
        button = (Button) findViewById( R.id.update_account );
        textView1 = (TextView) findViewById( R.id.access_billing_ok );
        textView2 = (TextView) findViewById( R.id.access_billing_deny );
        ButtonExit = (Button) findViewById( R.id.exit );

        ((BaseApplication) getApplication()).getActivityComponent().inject( this );
        presenter.bind( this );

        button.setOnClickListener( v -> presenter.onBillingButtonClick() );

        ButtonExit.setOnClickListener( v -> presenter.onLogout() );

    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i( "sign_in_form" );
    }

    @Override
    protected void onDestroy() {
        Timber.d( "onDestroy" );
        super.onDestroy();
    }

    @Override
    public void goToBillingView() {
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse( "https://r.loyverse.com/dashboard/#/settings/account" ) );
        startActivity( browserIntent );
    }


    @Override
    public void comeBackToLoginMenu() {
        presenter.onExitButtonClick();


    }

    @Override
    public void exitFromBilling() {
        navigator.showLoginScreen( this );
    }


    @Override
    public void showBillingExitDialog() {
        Timber.v( Utils.formatBreadCrumb( TAG, "Show billingOut dialog" ) );
        navigator.showBillingExitDialog( this );
    }

    @Override
    public void showButtonBilling() {
        button.setVisibility( View.VISIBLE );
        textView1.setVisibility( View.VISIBLE );
        textView2.setVisibility( View.GONE );

    }

    @Override
    public void hideButtonBilling() {
        button.setVisibility( View.GONE );
        textView1.setVisibility( View.GONE );
        textView2.setVisibility( View.VISIBLE );

    }
}
