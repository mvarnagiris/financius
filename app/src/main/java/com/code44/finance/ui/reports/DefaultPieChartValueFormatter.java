package com.code44.finance.ui.reports;

import com.code44.finance.money.AmountFormatter;

import lecho.lib.hellocharts.formatter.PieChartValueFormatter;
import lecho.lib.hellocharts.model.SliceValue;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultPieChartValueFormatter implements PieChartValueFormatter {
    private final AmountFormatter amountFormatter;
    private final String currencyCode;

    public DefaultPieChartValueFormatter(AmountFormatter amountFormatter, String currencyCode) {
        this.amountFormatter = checkNotNull(amountFormatter, "AmountFormatter cannot be null.");
        checkArgument(checkNotNull(currencyCode).length() == 3, "Currency code length must be 3.");
        this.currencyCode = currencyCode;
    }

    @Override public int formatChartValue(char[] chars, SliceValue sliceValue) {
        final char[] fullText = amountFormatter.format(currencyCode, (long) sliceValue.getValue()).toCharArray();
        final int size = Math.min(chars.length, fullText.length);
        System.arraycopy(fullText, 0, chars, chars.length - size, size);
        return size;
    }
}
