package com.loyverse.dashboard.mvp.presenters;

import com.google.firebase.messaging.FirebaseMessaging;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.LoginPresenter;
import com.loyverse.dashboard.base.mvp.LoginView;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.LoginRequest;
import com.loyverse.dashboard.core.api.LoginResponse;

import java.util.Arrays;

import rx.functions.Action1;
import timber.log.Timber;

public class LoginPresenterImpl extends LoginPresenter<LoginView> {
    private volatile boolean isLoginInProcess = false;
    private Action1<Throwable> onLoginError = throwable -> {
        Timber.e(throwable);
        if (throwable instanceof ServerError) {
            ServerError error = (ServerError) throwable;
            if (error.getResult().equals(ServerResult.ACCESS_DENIED.result))
                view.showAccessRightsRequiredMsg();
            else if (error.getResult().equals(ServerResult.WRONG_PASS.result)
                    || error.getResult().equals(ServerResult.UNKNOWN_LOGIN_EMAIL.result)
                    || error.getResult().equals(ServerResult.USER_WAS_BLOCKED.result))
                view.showWrongEmailOrPasswordDialog();
            else view.showToastOnException(throwable);
        } else view.showToastOnException(throwable);
        view.hideLoadingDialog();
    };

    private Action1<LoginResponse> onNext = response -> {
        view.hideLoadingDialog();
        dataModel.saveLoginData(response.ownerId, response.cookie, response.merchantId);
        dataModel.updatePermissions(Arrays.asList(response.permissions));
        view.login();
    };

    public LoginPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void handleLogin(String email, String password, String deviceId, String deviceName) {
        if (isLoginInProcess || view == null) { //TODO: figure out why view is null here
            return;
        }

        view.showLoadingDialog();
        if (!Utils.isValidEmail(email)) {
            view.showInvalidEmailMsg();
            return;
        }

        isLoginInProcess = true;
        dataModel.setEmail(email);
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            final String token;
            if (task.isSuccessful()) {
                token = task.getResult(); // Get new FCM registration token
            } else token = "";

            subscriptions.add(server.request(new LoginRequest(
                            email,
                            password,
                            deviceId,
                            deviceName,
                            token
                    ),
                    LoginResponse.class,
                    onNext,
                    onLoginError
            ));

            isLoginInProcess = false;
        });
    }

    @Override
    public void handleIfAlreadyLoggedIn() {
        if (dataModel.isUserLoggedIn())
            view.login();
    }
}
