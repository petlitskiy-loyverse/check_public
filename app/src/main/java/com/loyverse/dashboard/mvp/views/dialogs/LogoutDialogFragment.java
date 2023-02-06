package com.loyverse.dashboard.mvp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.SettingsView;

import timber.log.Timber;

import static com.loyverse.dashboard.core.Navigator.CONTENT_LAYOUT;

public class LogoutDialogFragment extends AppCompatDialogFragment {

    public static final String TAG = "LogoutDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        builder.setMessage(getResources().getString(R.string.logout_dialog_msg));
        builder.setPositiveButton(R.string.logout, (dialog, which) -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Positive button click"));
            Fragment callingFragment = getActivity().getSupportFragmentManager().findFragmentById(CONTENT_LAYOUT);
            if(callingFragment!=null && callingFragment instanceof SettingsView)
                ((SettingsView)callingFragment).onLogoutConfirmation();
            dismissAllowingStateLoss();
        });
        builder.setNegativeButton(android.R.string.no, (dialog, which) -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Negative button click"));
            dismissAllowingStateLoss();
        });
        return builder.create();
    }
}
