package com.code44.finance.money.grouping;

import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.data.model.Transaction;

import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class IntervalAmountGroups<AG extends IntervalAmountGroups.IntervalAmountGroup> extends AmountGroups<AG> {
    private final List<Interval> intervals;
    private final long startTimestamp;
    private final int intervalLength;

    public IntervalAmountGroups(Interval interval, IntervalType intervalIntervalType) {
        final Period period = getPeriod(intervalIntervalType);

        intervals = new ArrayList<>();
        startTimestamp = interval.getStartMillis();
        intervalLength = period.getMillis();

        Interval currentInterval = new Interval(interval.getStart(), period);
        while (currentInterval.overlaps(interval)) {
            intervals.add(currentInterval);
            currentInterval = new Interval(currentInterval.getEnd(), period);
        }
    }

    @Override protected int getGroupCount(Transaction transaction) {
        return 1;
    }

    @Override protected Long getGroupId(Transaction transaction, int groupPosition) {
        final int intervalPosition = (int) ((transaction.getDate() - startTimestamp) / intervalLength);
        if (intervalPosition < 0 || intervalPosition >= intervals.size()) {
            return null;
        }

        return intervals.get(intervalPosition).getStartMillis();
    }

    @Override protected AG createAmountGroup(Transaction transaction, int groupPosition) {
        final int intervalPosition = (int) ((transaction.getDate() - startTimestamp) / intervalLength);
        return createIntervalAmountGroup(intervals.get(intervalPosition), transaction);
    }

    @Override protected List<AG> getGroups(Collection<AG> groups) {
        final List<AG> sortedGroups = new ArrayList<>(groups);
        Collections.sort(sortedGroups, new Comparator<AG>() {
            @Override public int compare(AG lhs, AG rhs) {
                final long delta = lhs.getInterval().getStartMillis() - rhs.getInterval().getStartMillis();
                if (delta > 0) {
                    return 1;
                } else if (delta < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return null;
    }

    protected abstract AG createIntervalAmountGroup(Interval interval, Transaction transaction);

    private Period getPeriod(IntervalType intervalIntervalType) {
        switch (intervalIntervalType) {
            case Day:
                return Period.hours(1);
            case Week:
            case Month:
                return Period.days(1);
            case Year:
                return Period.months(1);
            default:
                throw new IllegalArgumentException("Type " + intervalIntervalType + " is not supported.");
        }
    }

    public static class IntervalAmountGroup extends AmountGroup {
        private final Interval interval;

        public IntervalAmountGroup(Interval interval) {
            this.interval = interval;
        }

        public Interval getInterval() {
            return interval;
        }
    }
}
