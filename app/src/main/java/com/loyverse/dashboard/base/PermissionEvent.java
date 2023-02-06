package com.loyverse.dashboard.base;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PermissionEvent {
    public static final int ACCESS_DENIED = 1;
    public static final int BLOCKED_BY_BILLING = 2;
    private
    @PermissionEvent.PermissionEventType
    int type;

    public int getType() {
        return type;
    }

    public PermissionEvent setType(int type) {
        this.type = type;
        return this;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ACCESS_DENIED, BLOCKED_BY_BILLING})
    public @interface PermissionEventType {
    }
}
