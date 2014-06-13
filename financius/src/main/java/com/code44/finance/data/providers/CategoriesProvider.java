package com.code44.finance.data.providers;

import android.net.Uri;

import com.code44.finance.data.db.Tables;

public class CategoriesProvider extends BaseModelProvider {
    public static Uri uriCategories() {
        return uriModels(CategoriesProvider.class, Tables.Categories.TABLE_NAME);
    }

    public static Uri uriCategory(long categoryId) {
        return uriModel(CategoriesProvider.class, Tables.Categories.TABLE_NAME, categoryId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Categories.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable();
    }
}
