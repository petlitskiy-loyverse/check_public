package com.loyverse.dashboard.base;


import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.core.Navigator;

import javax.inject.Inject;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.timber.TimberModule;

public abstract class BaseActivity extends AppCompatActivity {
    @Inject
    protected Navigator navigator;
    private ProgressDialog progressDialog;
    private DebugDrawer debugDrawer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
    }

    protected void addDebugDrawer() {
        debugDrawer = new DebugDrawer.Builder(this)
                .modules(
                        new TimberModule(),
                        new SettingsModule(),
                        new BuildModule(),
                        new NetworkModule(),
                        new DeviceModule()
                )
                .build();

    }

    public void showToastOnException(Throwable throwable) {
        Utils.showSnackBar(getWindow().getDecorView().getRootView(), Utils.getMessageForThrowable(getApplicationContext(), throwable));
    }

    @Override
    protected void onStop() {
        dismissProgressDialog();
        super.onStop();
    }

    public void showLoadingDialog() {
        if(isFinishing()){ //TODO: figure out why this method called after the finish callback
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }


    public void hideLoadingDialog() {
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }
}
