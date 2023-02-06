package com.loyverse.dashboard.mvp.views.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.BasePeriodView;

import timber.log.Timber;

import static com.loyverse.dashboard.core.Navigator.CONTENT_LAYOUT;

public class SelectPeriodDialogFragment extends AppCompatDialogFragment {
    public static final String TAG = "SelectPeriodDialog";
    public static final String CURRENT_POSITION_KEY = "current_position";

    private int position;

    public static SelectPeriodDialogFragment newInstance(int position) {
        SelectPeriodDialogFragment f = new SelectPeriodDialogFragment();
        Bundle args = new Bundle();
        args.putInt(CURRENT_POSITION_KEY, position);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        Timber.v(Utils.formatBreadCrumb(TAG, "onSaveInstanceState"));
        super.onSaveInstanceState(icicle);
        Timber.v(Utils.formatBreadCrumb(TAG, CURRENT_POSITION_KEY, String.valueOf(position)));
        icicle.putInt(CURRENT_POSITION_KEY, position);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View titleView = inflater.inflate(R.layout.layout_dialog_title, (ViewGroup) getView(), false);
        if (savedInstanceState == null)
            position = getArguments().getInt(CURRENT_POSITION_KEY);
        else
            position = savedInstanceState.getInt(CURRENT_POSITION_KEY);

        String[] valuesArray = getContext().getResources().getStringArray(R.array.period_array);
        return new AlertDialog.Builder(getActivity(), R.style.DialogTheme_Selection)
                .setCustomTitle(titleView)
                .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_calendar))
                .setSingleChoiceItems(valuesArray, position, (dialog, which) -> {
                    String period_tag = null;
                    switch (which) {
                        case 0:
                            period_tag = "today";
                            break;
                        case 1:
                            period_tag = "this_week";
                            break;
                        case 2:
                            period_tag = "this_month";
                            break;
                        case 3:
                            period_tag = "this_year";
                            break;
                        default:
                            period_tag = "custom";
                            break;
                    }
                    Timber.i("select_date_range date_range " + period_tag);
                    Timber.v(Utils.formatBreadCrumb(TAG, "Choose item", String.valueOf(which)));
                    Fragment callingFragment = getActivity().getSupportFragmentManager().findFragmentById(CONTENT_LAYOUT);
                    if (callingFragment != null && callingFragment instanceof BasePeriodView)
                        ((BasePeriodView) callingFragment).onPeriodLengthChanged(which);
                    dismissAllowingStateLoss();
                })
                .create();
    }
}