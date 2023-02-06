package com.loyverse.dashboard.base;

import android.content.Context;
import android.text.format.DateUtils;

import com.loyverse.dashboard.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PeriodUtils {

    public static final String DIVIDER_HOUR = "hour";
    public static final String DIVIDER_DAY = "day";
    public static final String DIVIDER_WEEK = "week";
    public static final String DIVIDER_MONTH = "month";
    public static final String DIVIDER_QUARTER = "quarter";//seems to not be used
    public static final String DIVIDER_YEAR = "year";
    public static final String DIVIDER_CUSTOM = "custom";

    public static String formatDatePeriod(long fromDate, long toDate, int period) {
        StringBuilder builder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        long currentYear = calendar.get(Calendar.YEAR);
        switch (period) {
            case Utils.DAY_PERIOD:
                calendar.setTimeInMillis(fromDate);
                builder.append(calendar.get(Calendar.DAY_OF_MONTH))
                        .append(" ")
                        .append(new SimpleDateFormat(" MMMM", Locale.getDefault()).format(fromDate));
                if (calendar.get(Calendar.YEAR) != currentYear)
                    builder.append(" ").append(calendar.get(Calendar.YEAR));
                break;
            case Utils.WEEK_PERIOD:
                calendar.setTimeInMillis(fromDate);
                builder.append(calendar.get(Calendar.DAY_OF_MONTH));
                if (isSame(Calendar.MONTH, fromDate, toDate)) {
                    if (!isSame(Calendar.DAY_OF_MONTH, fromDate, toDate)) {
                        builder.append(" - ");
                        calendar.setTimeInMillis(toDate);
                        builder.append(calendar.get(Calendar.DAY_OF_MONTH));
                    }
                    builder.append(" ")
                            .append(new SimpleDateFormat(" MMMM", Locale.getDefault()).format(fromDate));
                } else {
                    builder.append(" ")
                            .append(new SimpleDateFormat(" MMM", Locale.getDefault()).format(fromDate));
                    calendar.setTimeInMillis(toDate);
                    builder.append(" - ")
                            .append(calendar.get(Calendar.DAY_OF_MONTH)).append(" ")
                            .append(new SimpleDateFormat(" MMM", Locale.getDefault()).format(toDate));
                }
                if (calendar.get(Calendar.YEAR) != currentYear)
                    builder.append(" ").append(calendar.get(Calendar.YEAR));
                break;
            case Utils.MONTH_PERIOD:
                calendar.setTimeInMillis(fromDate);
                String month = new SimpleDateFormat("LLLL", Locale.getDefault()).format(fromDate);
                builder.append(month.substring(0, 1).toUpperCase()).append(month.substring(1));
                if (calendar.get(Calendar.YEAR) != currentYear)
                    builder.append(" ").append(calendar.get(Calendar.YEAR));
                break;
            case Utils.YEAR_PERIOD:
                calendar.setTimeInMillis(fromDate);
                builder.append(calendar.get(Calendar.YEAR));
                break;
            case Utils.CUSTOM_PERIOD:
                long now = Calendar.getInstance().getTimeInMillis();
                if (!isSame(Calendar.YEAR, fromDate, toDate) || !isSame(Calendar.YEAR, toDate, now)) {
                    calendar.setTimeInMillis(fromDate);
                    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
                    builder.append(df.format(calendar.getTime())).append(" - ");
                    calendar.setTimeInMillis(toDate);
                    builder.append(df.format(calendar.getTime()));
                } else
                    return formatDatePeriod(fromDate, toDate, (toDate - fromDate) > DateUtils.DAY_IN_MILLIS ? Utils.WEEK_PERIOD : Utils.DAY_PERIOD);
        }
        return builder.toString();
    }

    public static boolean isSame(int unit, long fromDate, long toDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate);
        int from = calendar.get(unit);
        calendar.setTimeInMillis(toDate);
        int to = calendar.get(unit);
        return from == to;
    }

    public static String formatYear(long fromDate, long toDate) {
        StringBuilder builder = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate);
        builder.append(calendar.get(Calendar.YEAR));
        if (!isSame(Calendar.YEAR, fromDate, toDate)) {
            calendar.setTimeInMillis(toDate);
            builder.append(" - ").append(calendar.get(Calendar.YEAR));
        }
        return builder.toString();
    }

    private static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static long getEndDate(long fromDate, int selectedPeriod, int customPeriodInDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate - 1);

        switch (selectedPeriod) {
            case Utils.DAY_PERIOD:
                calendar.add(Calendar.DATE, 1);
                break;
            case Utils.WEEK_PERIOD:
                calendar.add(Calendar.DATE, 7);
                break;
            case Utils.MONTH_PERIOD://some retarded shit right here
                calendar.add(Calendar.DATE, 1);
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                break;
            case Utils.YEAR_PERIOD:
                calendar.add(Calendar.YEAR, 1);
                break;
            case Utils.CUSTOM_PERIOD:
                calendar.add(Calendar.DATE, customPeriodInDays);
                break;
        }
        if (calendar.getTimeInMillis() > getEndDateFromToday())
            return getEndDateFromToday();
        else
            return calendar.getTimeInMillis();
    }

    public static long getStartDate(long toDate, int selectedPeriod, int customPeriodInDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(toDate);
        switch (selectedPeriod) {
            case Utils.DAY_PERIOD:
                break;
            case Utils.WEEK_PERIOD:
                calendar.add(Calendar.DAY_OF_MONTH, -1);//workaround because Calendar.SUNDAY is considered start of the week
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                break;
            case Utils.MONTH_PERIOD:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case Utils.YEAR_PERIOD:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
            case Utils.CUSTOM_PERIOD:
                if (customPeriodInDays > 1)
                    calendar.add(Calendar.DATE, -customPeriodInDays);
                break;
        }
        calendar.setTime(getStartOfDay(calendar.getTime()));
        return calendar.getTime().getTime();
    }

    //returns last millisecond of current day
    public static long getEndDateFromToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        calendar.setTime(getStartOfDay(calendar.getTime()));
        return calendar.getTimeInMillis() - 1;
    }

    public static long getStartDateFromToday(int selectedPeriod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getStartOfDay(calendar.getTime()));

        switch (selectedPeriod) {
            case Utils.DAY_PERIOD:
                break;
            case Utils.WEEK_PERIOD:
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                break;
            case Utils.MONTH_PERIOD:
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case Utils.YEAR_PERIOD:
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                break;
            default:
                break;
        }
        return calendar.getTime().getTime();
    }

    public static boolean canIncreasePeriod(long date) {
        Calendar calendar = Calendar.getInstance();
        long currentDate = calendar.getTimeInMillis();

        calendar.setTimeInMillis(date);
        long toDate = calendar.getTimeInMillis();

        return toDate <= currentDate;
    }

    public static String getDivider(int period, int customPeriodInDays, long fromDate) {
        switch (period) {
            case Utils.DAY_PERIOD:
                return DIVIDER_HOUR;
            case Utils.WEEK_PERIOD:
                return DIVIDER_DAY;
            case Utils.MONTH_PERIOD:
                return DIVIDER_DAY;
            case Utils.YEAR_PERIOD:
                return DIVIDER_MONTH;
            default:
                return getCustomDivider(customPeriodInDays, fromDate);
        }
    }

    private static String getCustomDivider(int periodInDays, long fromDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(fromDate);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

        int maxDaysAmountForDayDivider = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - dayOfMonth + 1;
        if (dayOfMonth != 1) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            maxDaysAmountForDayDivider += calendar.get(Calendar.DAY_OF_MONTH) - 1;
        }

        calendar.setTimeInMillis(fromDate);
        int maxDaysAmountForMonthDivider = calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - dayOfYear + 1;
        if (dayOfYear != 1) {
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.add(Calendar.YEAR, 1);
            maxDaysAmountForMonthDivider += calendar.getActualMaximum(Calendar.DAY_OF_YEAR) - 1;
        }

        if (periodInDays == 1)
            return DIVIDER_HOUR;
        else if (periodInDays <= maxDaysAmountForDayDivider)
            return DIVIDER_DAY;
        else if (periodInDays <= maxDaysAmountForMonthDivider)
            return DIVIDER_MONTH;
        else return DIVIDER_YEAR;
    }


    public static String getLegendByDivider(Context context, String divider) {
        switch (divider) {
            case DIVIDER_HOUR:
                return context.getResources().getString(R.string.hour);
            case DIVIDER_DAY:
                return context.getResources().getString(R.string.day);
            case DIVIDER_MONTH:
                return context.getResources().getString(R.string.month);
            case DIVIDER_QUARTER:
                return context.getResources().getString(R.string.quarter);
            case DIVIDER_YEAR:
                return context.getResources().getString(R.string.year);
            default:
                return "";
        }
    }

    public static String getCompareBy(int period) {
        switch (period) {
            case Utils.DAY_PERIOD:
                return DIVIDER_DAY;
            case Utils.WEEK_PERIOD:
                return DIVIDER_WEEK;
            case Utils.MONTH_PERIOD:
                return DIVIDER_MONTH;
            case Utils.YEAR_PERIOD:
                return DIVIDER_YEAR;
            default:
                return DIVIDER_CUSTOM;
        }
    }

    public static String formatTimePeriod(Integer startTime, Integer endTime) {
        if (startTime != null && endTime != null) {
            boolean is24hourFormat = android.text.format.DateFormat.is24HourFormat(BaseApplication.getAppContext());
            SimpleDateFormat format = new SimpleDateFormat(is24hourFormat ? "HH:mm" : "hh:mm a", Utils.EN_LOCALE);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MINUTE, 0);

            calendar.set(Calendar.HOUR_OF_DAY, startTime.intValue());
            final String start = format.format(calendar.getTime());

            calendar.set(Calendar.HOUR_OF_DAY, endTime.intValue());
            final String end = format.format(calendar.getTime());

            return start + " - " + end;
        }
        return null;
    }
}
