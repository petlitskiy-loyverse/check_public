package com.loyverse.dashboard.base.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.google.gson.GsonBuilder;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.core.api.Outlet;
import com.loyverse.dashboard.core.api.StockPushResponse;
import com.loyverse.dashboard.mvp.views.MainActivity;

import static com.loyverse.dashboard.core.DataModel.SEND_PUSHES;

public class StockJobIntentService extends JobIntentService {

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    public static final int REQUEST_CODE = 451;
    public static final String ACTION_SHOW_STOCK = "show_stock";
    private static int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    public static final String OUTLET_ID = "outletId";
    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, StockJobIntentService.class, JOB_ID, work);
    }
    @Override
    protected void onHandleWork(@NonNull Intent intent) {

        if (!ACTION_SHOW_STOCK.equals(intent.getAction()))
            return;

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Boolean showPushes = Utils.readFromSharedPreferences(getApplicationContext(), SEND_PUSHES, Boolean.class);
        if (showPushes != null && showPushes) {
            StockPushResponse.Ware[] wares = new GsonBuilder().disableHtmlEscaping().create().fromJson(intent.getStringExtra("wares"), StockPushResponse.Ware[].class);
            String outletName = intent.getStringExtra("outletName");
            int outletId = Integer.parseInt(intent.getStringExtra("outletId"));
            Outlet outlet = null;
            //if outletName == null then multi shop is disabled
            if (outletName != null && outletName.length() != 0) {
                outlet = new Outlet();
                outlet.id = outletId;
                outlet.name = outletName;
            }
            for (StockPushResponse.Ware ware : wares)
                showNotification(ware, outlet);
        }
    }

    private void showNotification(StockPushResponse.Ware ware, Outlet outlet) {
        long[] vibratePattern = {300L, 700L};
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(String.valueOf(System.currentTimeMillis()));
        if (outlet != null)
            intent.putExtra(OUTLET_ID, outlet.id);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        String content;
        if (outlet == null)
            content = ware.count <= 0 ?
                    String.format(getResources().getString(R.string.out_of_stock_push), ware.name) :
                    String.format(getResources().getString(R.string.low_stock_push), ware.name, Utils.formatNumber(ware.count / Utils.QUANTITY_DIVIDER));
        else
            content = ware.count <= 0 ?
                    String.format(getResources().getString(R.string.multishop_out_of_stock_push), ware.name, outlet.name) :
                    String.format(getResources().getString(R.string.multishop_low_stock_push), ware.name, outlet.name, Utils.formatNumber( ware.count / Utils.QUANTITY_DIVIDER));

        String adminChannelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, adminChannelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(vibratePattern)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID++, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.setting_stock_notification_title);
        String adminChannelDescription = getString(R.string.setting_stock_notification_description);
        String adminChannelId = getString(R.string.default_notification_channel_id);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(adminChannelId, adminChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }


}
