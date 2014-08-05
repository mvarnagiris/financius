package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;

import java.util.ArrayList;
import java.util.List;

public class GetCategoriesRequest extends FinanciusBaseRequest<Void> {
    public GetCategoriesRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected Void performRequest() throws Exception {
        long categoriesTimestamp = user.getCategoriesTimestamp();
        final List<CategoryEntity> categoryEntities = getCategoriesService().list(categoriesTimestamp).execute().getItems();
        final List<ContentValues> categoriesValues = new ArrayList<>();

        for (CategoryEntity entity : categoryEntities) {
            categoriesValues.add(Category.from(entity).asContentValues());
            if (categoriesTimestamp < entity.getEditTs()) {
                categoriesTimestamp = entity.getEditTs();
            }
        }

        DataStore.bulkInsert().values(categoriesValues).into(CategoriesProvider.uriCategories());
        user.setCategoriesTimestamp(categoriesTimestamp);
        return null;
    }
}
