package com.code44.finance.ui.reports.categories;

import com.code44.finance.data.model.Category;

import java.util.Comparator;
import java.util.Map;

class CategoryExpenseComparator implements Comparator<Category> {
    final Map<Category, Long> base;

    public CategoryExpenseComparator(Map<Category, Long> base) {
        this.base = base;
    }

    @Override public int compare(Category category1, Category category2) {
        final Long category1Total = base.get(category1);
        final Long category2Total = base.get(category2);
        if (category1Total > category2Total) {
            return 1;
        } else if (base.get(category1) < base.get(category2)) {
            return -1;
        } else {
            return 0;
        }
    }
}
