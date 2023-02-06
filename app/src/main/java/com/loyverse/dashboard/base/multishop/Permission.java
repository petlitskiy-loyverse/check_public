package com.loyverse.dashboard.base.multishop;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class Permission {
    public static final String ACCESS_REPORTS = "ACCESS_REPORTS";
    public static final String ACCESS_WARES = "ACCESS_WARES";
    public static final String ACCESS_BILLING = "ACCESS_BILLING";
    public static final String ACCESS_VIEW_COST = "ACCESS_VIEW_COST";

    private Permission() {

    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ACCESS_REPORTS, ACCESS_WARES})
    public @interface PermissionDef {
    }
}
