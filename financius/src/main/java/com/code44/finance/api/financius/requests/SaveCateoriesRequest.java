package com.code44.finance.api.financius.requests;

import android.content.Context;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.categories.model.CategoriesBody;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.data.db.model.Category;

import java.util.ArrayList;
import java.util.List;

public class SaveCateoriesRequest extends FinanciusBaseRequest<Void> {
    private final CategoriesBody body;

    public SaveCateoriesRequest(Context context, User user, List<Category> categories) {
        super(null, context, user);
        body = preparePostBody(categories);
    }

    @Override
    protected Void performRequest() throws Exception {
        getCategoriesService().save(body).execute();
        return null;
    }

    private CategoriesBody preparePostBody(List<Category> categories) {
        final List<CategoryEntity> categoryEntities = new ArrayList<>();
        for (Category category : categories) {
            categoryEntities.add(category.toEntity());
        }

        final CategoriesBody body = new CategoriesBody();
        body.setCategories(categoryEntities);
        body.setDeviceRegId(GcmRegistration.get().getRegistrationId());

        return body;
    }
}
