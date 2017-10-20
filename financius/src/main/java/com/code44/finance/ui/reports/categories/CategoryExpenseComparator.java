package com.code44.finance.ui.reports.categories;

import android.support.v4.util.Pair;

import com.code44.finance.data.model.Category;

import java.util.Comparator;

class CategoryExpenseComparator implements Comparator<Pair<Category, Long>> {
    @Override public int compare(Pair<Category, Long> leftValue, Pair<Category, Long> rightValue) {
        return (int) (rightValue.second - leftValue.second);
    }
}
