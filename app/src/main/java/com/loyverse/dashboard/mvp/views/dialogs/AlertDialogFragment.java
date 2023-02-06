package com.loyverse.dashboard.mvp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.loyverse.dashboard.base.Utils;

import timber.log.Timber;

public class AlertDialogFragment extends AppCompatDialogFragment {
    public static final String TAG = "AlertDialog";

    private String title;
    private String msg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null)
            return;

        title = savedInstanceState.getString("title");
        msg = savedInstanceState.getString("msg");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Positive button click", title != null ? title : ""));
            dismissAllowingStateLoss();
        });
        return builder.create();
    }

    public AlertDialogFragment setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertDialogFragment setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("title",title);
        outState.putString("msg",msg);
        super.onSaveInstanceState(outState);
    }
}
