package com.loyverse.dashboard.mvp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.ResetPasswordView;

import timber.log.Timber;

public class EmailSentDialogFragment extends AppCompatDialogFragment {
    public static final String TAG = "EmailSentDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String dialogTitle = getResources().getString(R.string.email_sent_dialog_title);
        final String dialogMsg = getResources().getString(R.string.email_sent_dialog_msg);

        builder.setTitle(dialogTitle);
        builder.setMessage(dialogMsg);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Positive button click"));
            ((ResetPasswordView) getActivity()).onSuccessEmailSent();
            dismissAllowingStateLoss();
        });

        AlertDialog dialog = builder.create();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = getActivity().getResources().getDimensionPixelSize(R.dimen.reset_dialog_width);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setAttributes(layoutParams);
        dialog.show();
        return dialog;
    }
}