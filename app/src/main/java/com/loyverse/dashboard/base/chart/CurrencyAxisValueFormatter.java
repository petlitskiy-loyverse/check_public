package com.loyverse.dashboard.base.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public CurrencyAxisValueFormatter() {
        mFormat = new DecimalFormat("###,###");
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(' ');
        mFormat.setDecimalFormatSymbols(formatSymbols);
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormat.format(value);
    }

}