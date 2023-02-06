package com.loyverse.dashboard.base;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;

public class UtilsTest {
    @Test
    public void formatCoinValue() {
        long value = 123456789;
        double requiredValue = 1234567.89D;
        assertThat(Utils.formatCoinValue(value), is(requiredValue));

        value = 123000;
        requiredValue = 1230;
        assertThat(Utils.formatCoinValue(value), is(requiredValue));
    }

    @Test
    public void calculatePercentageDifference() {
        double error = 0.01;

        long a = 7;
        long b = 10;
        double required = -30D;
        assertThat(Utils.calculatePercentageIncrease(a, b), is(closeTo(required, error)));

        a = 10;
        b = 7;
        required = 42.85D;
        assertThat(Utils.calculatePercentageIncrease(a, b), is(closeTo(required, error)));
    }

//    @Test
//    public void totalValue_
}
