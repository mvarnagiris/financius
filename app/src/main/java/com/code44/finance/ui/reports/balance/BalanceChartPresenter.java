package com.code44.finance.ui.reports.balance;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.AmountGrouper;
import com.code44.finance.ui.common.presenters.Presenter;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.interval.BaseInterval;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.formatter.LineChartValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;

public abstract class BalanceChartPresenter extends Presenter {
    private final BalanceChartView balanceChartView;
    private final AmountFormatter amountFormatter;

    public BalanceChartPresenter(BalanceChartView balanceChartView, AmountFormatter amountFormatter) {
        this.balanceChartView = balanceChartView;
        this.amountFormatter = amountFormatter;
    }

    public void setData(Account account, Cursor cursor, BaseInterval baseInterval) {
        final AmountGrouper.AmountCalculator amountCalculator = getTransactionValidator(account);
        final AmountGrouper amountGrouper = new AmountGrouper(baseInterval.getInterval(), baseInterval.getIntervalType());
        final Map<AmountGrouper.AmountCalculator, List<Long>> groups = amountGrouper.getGroups(cursor, amountCalculator);

        final List<Line> lines = new ArrayList<>();
        final Line line = getLine(account, groups.get(amountCalculator)).setColor(ThemeUtils.getColor(balanceChartView.getContext(), R.attr.textColorNeutral))
                .setHasLabels(true)
                .setHasLabelsOnlyForSelected(true);
        onLineCreated(amountCalculator, line);
        lines.add(line);

        final LineChartData lineChartData = new LineChartData(lines);
        lineChartData.setAxisXBottom(getAxis(baseInterval));

        balanceChartView.setLineGraphData(lineChartData);
    }

    protected abstract AmountGrouper.AmountCalculator getTransactionValidator(Account account);

    protected abstract void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line);

    protected Context getContext() {
        return balanceChartView.getContext();
    }

    private Line getLine(Account account, List<Long> amounts) {
        final List<PointValue> points = new ArrayList<>();
        int index = amounts.size() - 1;
        long totalAmount = account.getBalance();
        Collections.reverse(amounts);
        for (Long amount : amounts) {
            points.add(new PointValue(index, totalAmount));
            totalAmount -= amount;
            index--;
        }

        final int lineWidthDp = (int) (balanceChartView.getResources().getDimension(R.dimen.report_line_graph_width) / Resources.getSystem()
                .getDisplayMetrics().density);
        return new Line(points).setCubic(true)
                .setStrokeWidth(lineWidthDp)
                .setPointRadius(lineWidthDp)
                .setFormatter(new Formatter(amountFormatter, account.getCurrencyCode()))
                .setHasPoints(false);
    }

    private Axis getAxis(BaseInterval baseInterval) {
        final List<AxisValue> values = new ArrayList<>();
        final Period period = BaseInterval.getSubPeriod(baseInterval.getIntervalType(), baseInterval.getLength());

        Interval interval = new Interval(baseInterval.getInterval().getStart(), period);
        int index = 0;
        while (interval.overlaps(baseInterval.getInterval())) {
            values.add(new AxisValue(index++, BaseInterval.getSubTypeShortestTitle(interval, baseInterval.getIntervalType())
                    .toCharArray()));
            interval = new Interval(interval.getEnd(), period);
        }

        return new Axis(values).setHasLines(true).setHasSeparationLine(true);
    }

    private static class Formatter implements LineChartValueFormatter {
        private final AmountFormatter amountFormatter;
        private final String currencyCode;

        public Formatter(AmountFormatter amountFormatter, String currencyCode) {
            this.amountFormatter = amountFormatter;
            this.currencyCode = currencyCode;
        }

        @Override public int formatChartValue(char[] chars, PointValue pointValue) {
            final char[] fullText = amountFormatter.format(currencyCode, (long) pointValue.getY()).toCharArray();
            final int size = Math.min(chars.length, fullText.length);
            System.arraycopy(fullText, 0, chars, chars.length - size, size);
            return size;
        }
    }
}
