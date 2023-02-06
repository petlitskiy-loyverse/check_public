package com.loyverse.dashboard.base;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PeriodUtilsTest {
    @Test
    public void canIncreasePeriod_correctPeriod_true() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        final long today = calendar.getTime().getTime();
        assertTrue(PeriodUtils.canIncreasePeriod(today));

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        final long yesterday = calendar.getTime().getTime();
        assertTrue(PeriodUtils.canIncreasePeriod(yesterday));
    }

    @Test
    public void canIncreasePeriod_incorrectPeriod_false() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long tomorrow = calendar.getTime().getTime();
        assertFalse(PeriodUtils.canIncreasePeriod(tomorrow));
    }

    @Test
    public void correctCustomPeriodDivider() {
//        assertEquals(PeriodUtils.getDivider(Utils.CUSTOM_PERIOD, 1, 1L), PeriodUtils.DIVIDER_HOUR);
//        assertEquals(PeriodUtils.getDivider(Utils.CUSTOM_PERIOD, 2, 1L), PeriodUtils.DIVIDER_DAY);
//        assertEquals(PeriodUtils.getDivider(Utils.CUSTOM_PERIOD, 31, 1L), PeriodUtils.DIVIDER_MONTH);
//        assertEquals(PeriodUtils.getDivider(Utils.CUSTOM_PERIOD, 365, 1L), PeriodUtils.DIVIDER_MONTH);
//        assertEquals(PeriodUtils.getDivider(Utils.CUSTOM_PERIOD, 366, 1L), PeriodUtils.DIVIDER_YEAR);
//        assertEquals(PeriodUtils.getDivider(Utils.CUSTOM_PERIOD, 9999, 1L), PeriodUtils.DIVIDER_YEAR);
    }
    /*
    @Test
    public void correctYearFormat() {
        assertEquals(PeriodUtils.formatYear(1, 2), "1970");
        assertEquals(PeriodUtils.formatYear(1, 1 + DateUtils.YEAR_IN_MILLIS + DateUtils.WEEK_IN_MILLIS), "1970 - 1971");
    }

    @Test
    public void correctDateFormat() {
        assertEquals(PeriodUtils.formatDatePeriod(1, 2, Utils.DAY_PERIOD), "1 " + new SimpleDateFormat(" MMMM", Locale.getDefault()).format(1));
        assertEquals(PeriodUtils.formatDatePeriod(1, 1 + DateUtils.DAY_IN_MILLIS * 4, Utils.WEEK_PERIOD), "1 - 5 " + new SimpleDateFormat(" MMMM", Locale.getDefault()).format(1));
        assertEquals(PeriodUtils.formatDatePeriod(1, 1 + DateUtils.WEEK_IN_MILLIS, Utils.MONTH_PERIOD), "1 - 8 " + new SimpleDateFormat(" MMMM", Locale.getDefault()).format(1));
        assertEquals(PeriodUtils.formatDatePeriod(1, 2 + DateUtils.YEAR_IN_MILLIS + DateUtils.WEEK_IN_MILLIS, Utils.YEAR_PERIOD),
                "1 " + new SimpleDateFormat(" MMM", Locale.getDefault()).format(1)+ " - 7 " +  new SimpleDateFormat(" MMM", Locale.getDefault()).format(2 + DateUtils.YEAR_IN_MILLIS + DateUtils.WEEK_IN_MILLIS));
    }
    */
}
