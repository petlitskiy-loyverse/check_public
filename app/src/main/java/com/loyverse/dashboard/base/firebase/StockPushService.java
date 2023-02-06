package com.loyverse.dashboard.base.firebase;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class StockPushService extends FirebaseMessagingService {



    public StockPushService() {
        super();
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Base checking of push notification
        String type = remoteMessage.getData().get("type");
        if (type == null || !type.equals("wares_updated"))
            return;


        Intent intent = new Intent(StockJobIntentService.ACTION_SHOW_STOCK);
        intent.putExtra("wares", remoteMessage.getData().get("wares"));
        intent.putExtra("outletName", remoteMessage.getData().get("outletName"));
        intent.putExtra("outletId", remoteMessage.getData().get("outletId"));


        StockJobIntentService.enqueueWork(getApplicationContext(), intent);
        super.onMessageReceived(remoteMessage);
    }

    public static class StockChangedEvent {
        public boolean refreshData;
        public int outletId;

        public StockChangedEvent(boolean refreshData, int outletId) {
            this.refreshData = refreshData;
            this.outletId = outletId;
        }
    }

}
