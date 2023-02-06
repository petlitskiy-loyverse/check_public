package com.loyverse.dashboard.mvp.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseActivity;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.LoginPresenter;
import com.loyverse.dashboard.base.mvp.LoginView;
import com.loyverse.dashboard.base.server.NoNetworkException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.internal.Util;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements LoginView {
    private static final String LOG_CONTEXT = "LoginActivity";

    @BindView(R.id.password_field)
    TextInputEditText passwordEditText;

    @BindView(R.id.login_field)
    TextInputEditText loginEditText;

    @BindView(R.id.input_layout_login)
    TextInputLayout loginTextLayout;

    @Inject
    LoginPresenter<LoginView> presenter;


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        addDebugDrawer();
        ButterKnife.bind(this);

        ((BaseApplication) getApplication()).getActivityComponent().inject(this);
        presenter.bind(this);

        Utils.changeAppTheme(Utils.isDarkModeEnabled(this));

        if (savedInstanceState == null)
            presenter.handleIfAlreadyLoggedIn();
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onLoginButtonClick();
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.i("sign_in_form");
    }

    @Override
    protected void onDestroy() {
        Timber.d("onDestroy");
        presenter.unbind(this);
        super.onDestroy();
    }

    @Override
    public void login() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Login success"));
        Timber.i("sign_in_successful");
        navigator.showMainScreen(this);
    }

    @Override
    public void showWrongEmailOrPasswordDialog() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Show wrong email or password dialog"));
        hideLoadingDialog();
        navigator.showWrongEmailOrPasswordDialog(this);
    }



    @Override
    public void showInvalidEmailMsg() {
        hideLoadingDialog();
        String msg = getResources().getString(R.string.error_invalid_email);
        loginTextLayout.setErrorEnabled(true);
        loginTextLayout.setError(msg);
    }

    @Override
    public void showAccessRightsRequiredMsg() {
        navigator.showAccessRightsRequiredDialog(this);
        hideLoadingDialog();
    }


    @OnClick(R.id.new_to_loyverse)
    void onNewToLoyverseClick() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "New to loyverse button click"));
        navigator.showPosInformationScreen(this);
    }

    @OnClick(R.id.forgot_password)
    void onForgotPasswordClick() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Forgot password button click"));
        navigator.showResetPasswordScreen(this, loginEditText.getText().toString());
    }


    @OnClick(R.id.login_button)
    void onLoginButtonClick() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Login button click"));
        if (getCurrentFocus() != null)
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        if (Utils.isNetworkAvailable(getApplicationContext())) {
            loginTextLayout.setErrorEnabled(false);
            presenter.handleLogin(loginEditText.getText().toString(), passwordEditText.getText().toString(), Utils.getDeviceId(getApplicationContext()), android.os.Build.MODEL);
        } else
            showToastOnException(new NoNetworkException(getResources().getString(R.string.no_connection)));
    }
}
