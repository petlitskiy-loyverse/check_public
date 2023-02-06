package com.loyverse.dashboard.base.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.loyverse.dashboard.base.PeriodUtils;
import com.loyverse.dashboard.core.api.EarningsReportResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.loyverse.dashboard.base.PeriodUtils.DIVIDER_DAY;
import static com.loyverse.dashboard.base.PeriodUtils.DIVIDER_HOUR;
import static com.loyverse.dashboard.base.PeriodUtils.DIVIDER_MONTH;
import static com.loyverse.dashboard.base.PeriodUtils.DIVIDER_QUARTER;
import static com.loyverse.dashboard.base.PeriodUtils.DIVIDER_WEEK;
import static com.loyverse.dashboard.base.PeriodUtils.DIVIDER_YEAR;

public class PeriodAxisValueFormatter implements IAxisValueFormatter {

    private String divider = PeriodUtils.DIVIDER_HOUR;

    private SimpleDateFormat dayFormatter = new SimpleDateFormat("dd MMM", Locale.getDefault());
    private SimpleDateFormat monthFormatter = new SimpleDateFormat("LLL", Locale.getDefault());
    private SimpleDateFormat monthWithYearFormatter = new SimpleDateFormat("LLL y", Locale.getDefault());

    private EarningsReportResponse.EarningsRow[] rows;
    private boolean showMonthWithYear = false;

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        if ((long) value < rows.length && rows.length != 0)
            calendar.setTimeInMillis(rows[(int) value].from);
        switch (divider) {
            case DIVIDER_HOUR:
                return String.format(Locale.getDefault(), "%d:00", calendar.get(Calendar.HOUR_OF_DAY));
            case DIVIDER_DAY:
                return dayFormatter.format(calendar.getTime());
            case DIVIDER_WEEK:
                return dayFormatter.format(calendar.getTime());
            case DIVIDER_MONTH:
                if(showMonthWithYear)
                    return monthWithYearFormatter.format(calendar.getTime());
                else
                    return monthFormatter.format(calendar.getTime());
            case DIVIDER_QUARTER:
                return String.valueOf(value + 1);
            case DIVIDER_YEAR:
                return String.valueOf(calendar.get(Calendar.YEAR));
        }
        return String.valueOf(value);
    }

    public void setDivider(String divider, EarningsReportResponse.EarningsRow[] rows) {
        this.divider = divider;
        this.rows = rows;

        //showMonthWithYear check
        if(rows.length == 0 || !divider.equals(PeriodUtils.DIVIDER_MONTH))
            return;

        long now = Calendar.getInstance().getTimeInMillis();
        showMonthWithYear = !PeriodUtils.isSame(Calendar.YEAR, rows[0].from, rows[rows.length-1].from)
                || !PeriodUtils.isSame(Calendar.YEAR, rows[rows.length-1].from, now);
    }
}
