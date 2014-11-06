package com.code44.finance.ui.reports.categories;

import android.support.v4.util.Pair;

import com.code44.finance.data.model.Category;

import java.util.Comparator;

class CategoryExpenseComparator implements Comparator<Pair<Category, Long>> {
    @Override public int compare(Pair<Category, Long> leftValue, Pair<Category, Long> rightValue) {
        if (leftValue.second < rightValue.second) {
            return 1;
        } else if (leftValue.second > rightValue.second) {
            return -1;
        } else {
            return 0;
        }
    }
}
