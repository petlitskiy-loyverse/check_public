package com.loyverse.dashboard.base.server;

import com.loyverse.dashboard.R;

public enum ServerResult {
    WRONG_PASS("wrong_password", R.string.error_wrong_password),
    BAD_COOKIE_AUTH("bad_cookie_auth", R.string.error_bad_auth),
    DEVICE_UNASSIGNED("device_unassigned", R.string.error_bad_auth),
    ACCOUNT_VOIDED("account_voided", R.string.error_bad_auth),
    UNKNOWN_ERROR("", R.string.error_unknown),
    EMAIL_NOT_VALIDATED("email_not_validated", R.string.error_wrong_password),
    EMAIL_NOT_EXIST("email_not_exist", R.string.error_email_not_exist),
    OWNER_ALREADY_DELETED("owner_already_deleted", R.string.error_owner_already_deleted),
    DEVICE_NOT_EXIST("device_not_exist", R.string.no_such_device),
    ACCESS_DENIED("access_denied", R.string.access_denied),
    UNKNOWN_LOGIN_EMAIL("unknown_login_email", R.string.unknown_login_email),
    USER_WAS_BLOCKED("user_was_blocked", R.string.unknown_login_email),
    BLOCKED_BY_BILLING("blocked_by_billing",R.string.unknown_login_email ),
    OK("ok", 0);

    public final String result;
    public final int resource;

    ServerResult(String result, int resource) {
        this.result = result;
        this.resource = resource;
    }
}
