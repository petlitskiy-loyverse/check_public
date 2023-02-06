package com.loyverse.dashboard.mvp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.MainView;

import timber.log.Timber;


public class RetryDialog extends AppCompatDialogFragment {
    private static final String RETRY_MSG_ID = "retryMsgId";
    public static final String TAG = "RetryDialog";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(getMsgId()));
        builder.setPositiveButton(R.string.retry, (dialog, which) -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Retry button click"));
            dismissAllowingStateLoss();
            ((MainView) getActivity()).onRetryClick();
        });
        return builder.create();
    }

    private int getMsgId(){
        Bundle bundle = getArguments();
        if(bundle!=null){
            return bundle.getInt(RETRY_MSG_ID);
        } else return R.string.retry_dialog_no_internet_msg;
    }

    public static RetryDialog newInstance(int msgId) {
        RetryDialog outletsFragment = new RetryDialog();
        Bundle args = new Bundle();
        args.putInt(RETRY_MSG_ID, msgId);
        outletsFragment.setArguments(args);
        return outletsFragment;
    }
}
