package com.code44.finance.ui.reports;

import com.code44.finance.money.AmountFormatter;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.model.PointValue;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultLineChartValueFormatter implements LineChartValueFormatter {
    private final AmountFormatter amountFormatter;

    public DefaultLineChartValueFormatter(AmountFormatter amountFormatter) {
        this.amountFormatter = checkNotNull(amountFormatter, "AmountFormatter cannot be null.");
    }

    @Override public int formatChartValue(char[] chars, PointValue pointValue) {
        final char[] fullText = amountFormatter.format((long) pointValue.getY()).toCharArray();
        final int size = Math.min(chars.length, fullText.length);
        System.arraycopy(fullText, 0, chars, chars.length - size, size);
        return size;
    }
}
