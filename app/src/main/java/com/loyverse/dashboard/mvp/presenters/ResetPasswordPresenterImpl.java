package com.loyverse.dashboard.mvp.presenters;

import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.ResetPasswordPresenter;
import com.loyverse.dashboard.base.mvp.ResetPasswordView;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.core.api.RestorePasswordRequest;
import com.loyverse.dashboard.core.api.RestorePasswordResponse;

import rx.functions.Action1;
import timber.log.Timber;

public class ResetPasswordPresenterImpl extends ResetPasswordPresenter<ResetPasswordView> {

    private Action1<RestorePasswordResponse> onNext = response -> {
        Timber.d("Restore password");
        view.hideLoadingDialog();
        if (response.result.equals(ServerResult.OK.result))
            view.showEmailSentDialog();
        else if (response.result.equals(ServerResult.EMAIL_NOT_EXIST.result))
            view.showEmailNonExistDialog();
        else
            view.showToastOnException(new ServerError(response.result));
    };

    public ResetPasswordPresenterImpl(DataModel dataModel, Server server) {
        super(dataModel, server);
    }

    @Override
    public void resetPassword(String email) {
        if (!Utils.isValidEmail(email)) {
            view.showInvalidEmailMsg();
            return;
        }
        subscriptions.add(server.request(new RestorePasswordRequest(email),
                RestorePasswordResponse.class,
                onNext, onError));
    }
}
