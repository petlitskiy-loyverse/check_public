package com.loyverse.dashboard.mvp.views.dialogs;

import static com.loyverse.dashboard.core.Navigator.CONTENT_LAYOUT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.loyverse.dashboard.R;
import com.loyverse.dashboard.base.BaseApplication;
import com.loyverse.dashboard.base.Utils;
import com.loyverse.dashboard.base.mvp.BasePeriodView;
import com.loyverse.dashboard.mvp.calendar.CalendarDay;
import com.loyverse.dashboard.mvp.calendar.DateModel;
import com.loyverse.dashboard.mvp.calendar.VerticalCalendarView;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.Unit;
import timber.log.Timber;

public class CalendarDialogFragment extends AppCompatDialogFragment {
    public static final String TAG = "CalendarDialog";
    private static final String SAVED_TEXT_DATE_KEY = "saved_text_date_key";
    private static final String SAVED_FROM_DAY_KEY = "saved_from_day_key";
    private static final String SAVED_TO_DAY_KEY = "saved_to_day_key";
    private static final String SAVED_TO_START_TIME = "start_time";
    private static final String SAVED_TO_END_TIME = "end_time";
    private TextView tv_start_value, tv_end_value, tvSelectedTime;
    private SwitchCompat switch_allday;
    private String text = "";
    private long from = 0, to = 0;
    private boolean allDay = true;
    private String startTime = "", endTime = "";
    private Integer startTimeValue, endTimeValue = null;
    private final String[] values = new String[24];

    public static CalendarDialogFragment newInstance(long from, long to, String date, Integer startTime, Integer endTime) {
        CalendarDialogFragment f = new CalendarDialogFragment();
        Bundle args = new Bundle();
        args.putString(SAVED_TEXT_DATE_KEY, date);
        args.putLong(SAVED_FROM_DAY_KEY, from);
        args.putLong(SAVED_TO_DAY_KEY, to);
        if (startTime != null) {
            args.putString(SAVED_TO_START_TIME, String.valueOf(startTime));
            args.putString(SAVED_TO_END_TIME, String.valueOf(endTime));
        }
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseApplication) requireActivity().getApplication()).getActivityComponent().inject(this);
        setStyle(AppCompatDialogFragment.STYLE_NORMAL, R.style.FullScreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_dialog_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateStateSavedPeriod(savedInstanceState != null ? savedInstanceState : getArguments() != null ? getArguments() : null);

        VerticalCalendarView widget = view.findViewById(R.id.calendarView);
        ImageView btnBack = view.findViewById(R.id.btn_back);
        Button btnApply = view.findViewById(R.id.btn_apply);
        switch_allday = view.findViewById(R.id.switch_allday);
        switch_allday.setPaddingRelative(0, 0, Utils.dpToPx(switch_allday.isChecked() ? 15 : 3), 0);
        LinearLayout layout_start = view.findViewById(R.id.layout_start);
        LinearLayout layout_end = view.findViewById(R.id.layout_end);
        tv_start_value = view.findViewById(R.id.tv_start_value);
        tv_end_value = view.findViewById(R.id.tv_end_value);
        TextView tvSelectedPeriod = view.findViewById(R.id.tv_selected_period);
        tvSelectedTime = view.findViewById(R.id.tv_selected_time);
        if (text.isEmpty()) {
            text = getArguments() != null ? getArguments().getString(SAVED_TEXT_DATE_KEY) : null;
        }
        tvSelectedPeriod.setText(text);

        selectPrevDate(widget);

        switch_allday.setOnCheckedChangeListener(((buttonView, isChecked) ->
        {
            allDay = isChecked;
            if (isChecked) {
                layout_start.setVisibility(View.GONE);
                layout_end.setVisibility(View.GONE);
                tvSelectedTime.setVisibility(View.GONE);
                switch_allday.setPaddingRelative(0, 0, Utils.dpToPx(15), 0);
            } else {
                layout_start.setVisibility(View.VISIBLE);
                layout_end.setVisibility(View.VISIBLE);
                if (startTimeValue == null) {
                    startTimeValue = 0;
                    endTimeValue = 23;
                    tv_start_value.setText(values[startTimeValue]);
                    tv_end_value.setText(values[endTimeValue]);
                }
                showHours();
                switch_allday.setPaddingRelative(0, 0, Utils.dpToPx(3), 0);
            }
        }));

        layout_start.setOnClickListener(v ->
                showTimePickerDialog(getString(R.string.start), tv_start_value.getText().toString())
        );
        layout_end.setOnClickListener(v ->
                showTimePickerDialog(getString(R.string.end), tv_end_value.getText().toString())
        );

        widget.setListener((from, to) -> {
            tvSelectedPeriod.setText(formatTextRange(from.getDate(), to.getDate()));
            text = tvSelectedPeriod.getText().toString();
            this.from = from.getDate().getTime();
            this.to = to.getDate().getTime();
            return Unit.INSTANCE;
        });

        btnBack.setOnClickListener(button -> dismiss());
        btnApply.setOnClickListener(onClick -> {
            Timber.v(Utils.formatBreadCrumb(TAG, "Positive button click"));
            if (widget.getSelectedDates().size() < 1) {
                Snackbar.make(view, getResources().getString(R.string.select_range), Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (allDay) {
                startTimeValue = null;
                endTimeValue = null;
            } else {
                if (startTimeValue == null || endTimeValue == null) {
                    Snackbar.make(view, getResources().getString(R.string.select_start_endtime), Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            List<Date> dates = new ArrayList<>();
            for (CalendarDay calendarDay : widget.getSelectedDates())
                dates.add(calendarDay.getDate());
            Collections.sort(dates);
            Fragment callingFragment = requireActivity().getSupportFragmentManager().findFragmentById(CONTENT_LAYOUT);
            if (callingFragment instanceof BasePeriodView) {
                ((BasePeriodView) callingFragment).onCustomPeriodSelected(dates, startTimeValue, endTimeValue);
            }
            dismiss();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        onViewStateRestored();
    }

    private void onViewStateRestored() {
        createTimeArray();
        if (startTimeValue != null) {
            switch_allday.setChecked(false);
            tv_start_value.setText(values[startTimeValue]);
            tv_end_value.setText(values[endTimeValue]);
            showHours();
        } else {
            switch_allday.setChecked(true);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        onViewStateRestored();
    }

    private void createTimeArray() {
        boolean is24hourFormat = android.text.format.DateFormat.is24HourFormat(getActivity());
        if (is24hourFormat) {
            for (int i = 0; i < 24; i++) {
                String time = String.format(Locale.getDefault(), "%02d:00", i);
                values[i] = time;
            }
        } else {
            String time = "12:00 AM";
            values[0] = time;
            for (int i = 1; i <= 23; i++) {
                if (i < 12)
                    time = String.format(Locale.getDefault(), "%02d:00 AM", i);
                else if (i > 12)
                    time = String.format(Locale.getDefault(), "%02d:00 PM", (i - 12));
                else
                    time = String.format(Locale.getDefault(), "%02d:00 PM", i);

                values[i] = time;
            }
        }
    }

    private void showTimePickerDialog(String titleStr, String prevTime) {
        Dialog dialog = new Dialog(getActivity(), R.style.TimePickerDialog);
        dialog.setContentView(R.layout.dialog_timepicker);
        TextView title = dialog.findViewById(R.id.title);
        TextView ok = dialog.findViewById(R.id.ok);
        TimePicker timePicker = dialog.findViewById(R.id.timepicker);
        TextView cancel = dialog.findViewById(R.id.cancel);
        title.setText(titleStr);

        View view = (View) timePicker.findViewById(Resources.getSystem().getIdentifier("minute", "id", "android"));
        View viewAMPM = (View) timePicker.findViewById(Resources.getSystem().getIdentifier("amPm", "id", "android"));
        NumberPicker numberPicker = (NumberPicker) timePicker.findViewById(Resources.getSystem().getIdentifier("hour", "id", "android"));
        view.setVisibility(View.GONE);
        viewAMPM.setVisibility(View.GONE);
        View saparator = (View) timePicker.findViewById(Resources.getSystem().getIdentifier("divider", "id", "android"));
        saparator.setVisibility(View.GONE);
        timePicker.setIs24HourView(true);
        numberPicker.setDisplayedValues(values);
        if (!prevTime.isEmpty()) {
            int selectedHours = 0;
            for (int i = 0; i < values.length; i++) {
                if (prevTime.equals(values[i])) {
                    selectedHours = i;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(selectedHours);
            } else
                timePicker.setCurrentHour(selectedHours);
        }

        ok.setOnClickListener(v -> {
            dialog.dismiss();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hours = timePicker.getHour();
                showTime(titleStr, values[hours], hours);
            }
        });
        cancel.setOnClickListener(v ->
                dialog.dismiss()
        );
        dialog.show();

    }

    private void showTime(String titleStr, String time, int hours) {
        if (titleStr.equals(getString(R.string.start))) {
            tv_start_value.setText(time);
            startTime = time;
            startTimeValue = hours;
        } else {
            tv_end_value.setText(time);
            endTime = time;
            endTimeValue = hours;
        }
        Log.e("=====", "===start==" + startTimeValue + "  end  " + endTimeValue);
        showHours();
    }

    @SuppressLint("SetTextI18n")
    private void showHours() {
        final String start = tv_start_value.getText().toString();
        final String end = tv_end_value.getText().toString();
        if (!start.isEmpty() && !end.isEmpty()) {
            tvSelectedTime.setText(start + " - " + end);
            tvSelectedTime.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_TEXT_DATE_KEY, text);
        outState.putLong(SAVED_FROM_DAY_KEY, from);
        outState.putLong(SAVED_TO_DAY_KEY, to);
        if (startTimeValue != null) {
            outState.putString(SAVED_TO_START_TIME, String.valueOf(startTimeValue));
            outState.putString(SAVED_TO_END_TIME, String.valueOf(endTimeValue));
        }
    }

    private void updateStateSavedPeriod(Bundle savedInstanceState) {
        text = savedInstanceState.getString(SAVED_TEXT_DATE_KEY);
        from = savedInstanceState.getLong(SAVED_FROM_DAY_KEY);
        to = savedInstanceState.getLong(SAVED_TO_DAY_KEY);
        if (savedInstanceState.getString(SAVED_TO_START_TIME, null) != null) {
            startTimeValue = Integer.parseInt(savedInstanceState.getString(SAVED_TO_START_TIME, "-1"));
            endTimeValue = Integer.parseInt(savedInstanceState.getString(SAVED_TO_END_TIME, "-1"));
        }
    }

    private void selectPrevDate(VerticalCalendarView widget) {
        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(new Date(from));
        CalendarDay fromDate = mapCalendarToCalendarDay(calendarFrom);

        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(new Date(to));
        CalendarDay toDate = mapCalendarToCalendarDay(calendarTo);

        widget.getCalendarAdapter().selectDate(fromDate, toDate);
    }

    @NotNull
    private CalendarDay mapCalendarToCalendarDay(Calendar calendarFrom) {
        return new CalendarDay(
                calendarFrom.get(Calendar.YEAR),
                calendarFrom.get(Calendar.MONTH) + 1,
                calendarFrom.get(Calendar.DAY_OF_MONTH)
        );
    }

    private String formatTextRange(Date df, Date dt) {
        DateModel dm = new DateModel();
        final String output;

        if (df.equals(dt)) {
            DateFormat format = new SimpleDateFormat("MMMM dd", dm.getDeviceLocale());
            output = format.format(df);
//        } else if (dm.isEqualsMonth(df, dt)) {
//            DateFormat fromMonthFormat = new SimpleDateFormat("dd", dm.getDeviceLocale());
//            DateFormat toMonthFormat = new SimpleDateFormat("dd MMMM", dm.getDeviceLocale());
//            output = fromMonthFormat.format(df) + " - " + toMonthFormat.format(dt);
        } else if (dm.isEqualsYear(df, dt)) {
            DateFormat currentMonthFormat = new SimpleDateFormat("MMM dd", dm.getDeviceLocale());
            output = currentMonthFormat.format(df) + " - " + currentMonthFormat.format(dt);
        } else {
            DateFormat baseFormat = new SimpleDateFormat("dd/MM/yyyy", dm.getDeviceLocale());
            output = baseFormat.format(df) + " - " + baseFormat.format(dt);
        }

        return output;
    }
}