package com.code44.finance.ui.reports.categories;

import com.code44.finance.data.model.Tag;

import java.util.Comparator;
import java.util.Map;

class TagExpenseComparator implements Comparator<Tag> {
    final Map<Tag, Long> base;

    public TagExpenseComparator(Map<Tag, Long> base) {
        this.base = base;
    }

    @Override public int compare(Tag tag1, Tag tag2) {
        final Long tag1Total = base.get(tag1);
        final Long tag2Total = base.get(tag2);
        if (tag1Total > tag2Total) {
            return 1;
        } else if (base.get(tag1) < base.get(tag2)) {
            return -1;
        } else {
            return 0;
        }
    }
}
