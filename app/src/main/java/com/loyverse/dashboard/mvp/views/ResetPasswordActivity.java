package com.loyverse.dashboard.mvp.views;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseActivity;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.ResetPasswordPresenter;
import com.loyverse.dashboard.base.mvp.ResetPasswordView;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static com.loyverse.dashboard.core.Navigator.EMAIL_KEY;

public class ResetPasswordActivity extends BaseActivity implements ResetPasswordView {
    private static final String LOG_CONTEXT = "ResetPasswordActivity";

    @BindView(R.id.login_field)
    EditText loginTextEdit;

    @BindView(R.id.input_layout_login)
    TextInputLayout loginTextLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    ResetPasswordPresenter<ResetPasswordView> presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        addDebugDrawer();
        ButterKnife.bind(this);
        ((BaseApplication) getApplication()).getActivityComponent().inject(this);
        presenter.bind(this);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> finish());

        loginTextEdit.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onResetPasswordButtonClick();
            }
            return false;
        });
        initFields();
        Timber.i("reset_password_form");
    }

    private void initFields() {
        final String email = getIntent().getStringExtra(EMAIL_KEY);
        if (email != null) {
            loginTextEdit.setText(email);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unbind(this);
        super.onDestroy();
    }

    @OnClick(R.id.reset_password_button)
    void onResetPasswordButtonClick() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Reset password button click"));
        showLoadingDialog();
        loginTextLayout.setErrorEnabled(false);
        presenter.resetPassword(loginTextEdit.getText().toString());
    }

    @Override
    public void showEmailSentDialog() {
        Timber.i("reset_password_email_sent");
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Success email sending"));
        hideLoadingDialog();
        navigator.showEmailSentDialog(this);
    }

    @Override
    public void showEmailNonExistDialog() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Show email non exist dialog"));
        hideLoadingDialog();
        navigator.showEmailNotExistDialog(this);
    }

    @Override
    public void showInvalidEmailMsg() {
        Timber.v(Utils.formatBreadCrumb(LOG_CONTEXT, "Show invalid email msg"));
        hideLoadingDialog();
        String msg = getResources().getString(R.string.error_invalid_email);
        loginTextLayout.setErrorEnabled(true);
        loginTextLayout.setError(msg);
    }

    @Override
    public void onSuccessEmailSent() {
        finish();
    }


}
