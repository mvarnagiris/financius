package com.code44.finance.ui.reports.categories;

import android.support.v4.util.Pair;

import com.code44.finance.data.model.Tag;

import java.util.Comparator;

class TagExpenseComparator implements Comparator<Pair<Tag, Long>> {
    @Override public int compare(Pair<Tag, Long> leftValue, Pair<Tag, Long> rightValue) {
        return (int) (rightValue.second - leftValue.second);
    }
}
