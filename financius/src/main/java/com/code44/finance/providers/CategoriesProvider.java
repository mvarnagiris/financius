package com.code44.finance.providers;

import com.code44.finance.db.model.Category;

public class CategoriesProvider extends BaseModelProvider<Category> {
    @Override
    protected Class<Category> getModelClass() {
        return Category.class;
    }
}
