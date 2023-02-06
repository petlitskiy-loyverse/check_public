package com.loyverse.dashboard.base.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import timber.log.Timber;

public class TokenService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        //is called when app is installed, check out how logout is handled in POS
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.d("TOKEN %s", refreshedToken);
    }
}
