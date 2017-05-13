package com.code44.finance.money.grouping;

import com.code44.finance.data.model.Category;

public class CategoryGroup extends AmountGroups.AmountGroup {
    private final Category category;

    public CategoryGroup(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
