package com.loyverse.dashboard.base.chart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.Locale;


public class MultiLangLargeValueFormatter implements IValueFormatter, IAxisValueFormatter {
    private static final int MAX_LENGTH = 3;
    private static final int E_SUFFIX_LENGTH = 3;
    private String[] SUFFIX = new String[]{
            "", "k", "m", "b", "t"
    };
    private final DecimalFormat mFormat;
    private String mText = "";

    public MultiLangLargeValueFormatter() {
        mFormat = new DecimalFormat("###E00");
    }

    public MultiLangLargeValueFormatter(String[] suffix) {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        mFormat = new DecimalFormat("###E00");
        setSuffix(suffix);
        Locale.setDefault(defaultLocale);
    }

    /**
     * Creates a formatter that appends a specified text to the result string
     *
     * @param appendix a text that will be appended
     */
    public MultiLangLargeValueFormatter(String appendix) {
        this();
        mText = appendix;
    }

    // IValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return makePretty(value) + mText;
    }

    // IAxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return makePretty(value) + mText;
    }

    /**
     * Set an appendix text to be added at the end of the formatted value.
     *
     * @param appendix
     */
    public void setAppendix(String appendix) {
        this.mText = appendix;
    }

    /**
     * Set custom suffix to be appended after the values.
     * Default suffix: ["", "k", "m", "b", "t"]
     *
     * @param suff new suffix
     */
    public void setSuffix(String[] suff) {
        SUFFIX = suff;
    }

    /**
     * Formats each number properly. Special thanks to Roman Gromov
     * (https://github.com/romangromov) for this piece of code.
     */
    private String makePretty(double number) {
        if (number < 1 && number > -1) {
            if (number == 0)
                return "0";
            return String.format(Locale.US, "%.2f", number);
        }

        String r = mFormat.format(number);

        int numericValue1 = Character.getNumericValue(r.charAt(r.length() - 1));
        int numericValue2 = Character.getNumericValue(r.charAt(r.length() - 2));
        int index = Integer.parseInt(numericValue2 + "" + numericValue1) / 3;
        // TODO: 06.12.16 Refactor substring overhead (suffix)
        final String suffix = SUFFIX[index];
        r = r.replace(",", ".").substring(0, r.length() - E_SUFFIX_LENGTH);

        int indexOfCommaSymbol = r.indexOf('.');
        if (indexOfCommaSymbol == -1) {
            return r + suffix;
        }
        double tmp;
        try {
            tmp = Double.parseDouble(r);
        } catch (NumberFormatException e) {
            //server sends number in a weird form
            tmp = Double.parseDouble(String.format(Locale.US, "%.2f", number));
        }
        if (index > 0)
            r = String.format(Locale.US, "%.1f", tmp);
        else
            r = String.format(Locale.US, "%.2f", tmp);

        int maxLength = MAX_LENGTH;
        if (r.charAt(0) == '-')
            maxLength++;

        while (r.length() > maxLength || isUselessNumber(r, r.length() - 1)) {
            r = r.substring(0, r.length() - 1);
        }

        return r + suffix;
    }

    private boolean isUselessNumber(String str, int charIndex) {
        if (charIndex < 0 || (str.indexOf(',') == -1 && str.indexOf('.') == -1))
            return false;

        char c = str.charAt(charIndex);
        return c == '0' || c == ',' || c == '.';
    }

}
