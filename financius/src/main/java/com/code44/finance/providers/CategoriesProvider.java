package com.code44.finance.providers;

import android.content.ContentUris;
import android.net.Uri;

import com.code44.finance.db.model.Category;

public class CategoriesProvider extends BaseModelProvider<Category> {
    public static Uri uriCategories() {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(CategoriesProvider.class) + "/" + Category.class.getSimpleName());
    }

    public static Uri uriCategory(long categoryId) {
        return ContentUris.withAppendedId(uriCategories(), categoryId);
    }

    @Override
    protected Class<Category> getModelClass() {
        return Category.class;
    }
}
