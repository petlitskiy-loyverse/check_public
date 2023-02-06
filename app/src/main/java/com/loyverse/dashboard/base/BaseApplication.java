package com.loyverse.dashboard.base;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.loyverse.dashboard.BuildConfig;
import com.loyverse.dashboard.base.dagger.ActivityComponent;
import com.loyverse.dashboard.base.dagger.ApplicationComponent;
import com.loyverse.dashboard.base.dagger.DaggerActivityComponent;
import com.loyverse.dashboard.base.dagger.DaggerApplicationComponent;
import com.loyverse.dashboard.base.dagger.module.DataModelModule;
import io.palaima.debugdrawer.timber.data.LumberYard;
import timber.log.Timber;

public class BaseApplication extends Application {

    private ApplicationComponent applicationComponent;
    private ActivityComponent activityComponent;
    private static Context mAppContext;

    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG_BUILD) {
            LumberYard lumberYard = LumberYard.getInstance(this);
            lumberYard.cleanUp();
            Timber.plant(lumberYard.tree());
            Timber.plant(new Timber.DebugTree());
            Timber.plant(new CrashReportingTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        applicationComponent = DaggerApplicationComponent.builder().dataModelModule(new DataModelModule(getApplicationContext())).build();
        activityComponent = DaggerActivityComponent.builder().applicationComponent(getApplicationComponent()).build();
        mAppContext = this;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    /**
     * Log.INFO               - for analytics user events
     * Log.DEBUG              - for debug logging
     * Log.VERBOSE            - for Crashlytics/Firebase logging
     * Log.WARN               - for warnings reporting
     * Log.ERROR              - for crash reporting
     **/

    private class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.DEBUG)
                return;
            if (priority == Log.VERBOSE)
                FirebaseCrashlytics.getInstance().log(message);
            if (priority == Log.INFO) {
                String[] messageArray = message.split("\\s+");
                Bundle firebaseBundle = new Bundle();
                if (messageArray.length == 3) {//hardcoded to contain only 1 parameter
                    firebaseBundle.putString(messageArray[1], messageArray[2]);
                }
                firebaseAnalytics.logEvent(messageArray[0], firebaseBundle);
            }
            if (t != null) {
                if (priority == Log.ERROR) {
                    FirebaseCrashlytics.getInstance().recordException(t);
                } else if (priority == Log.WARN) {
                    FirebaseCrashlytics.getInstance().log(message);
                }
            }
        }
    }
}
