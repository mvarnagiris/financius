package com.code44.finance.api.requests;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.backend.endpoint.categories.Categories;
import com.code44.finance.backend.endpoint.categories.model.CategoriesBody;
import com.code44.finance.backend.endpoint.categories.model.CategoryEntity;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Category;

import java.util.ArrayList;
import java.util.List;

public class PostCategoriesRequest extends PostRequest<CategoriesBody> {
    private final Categories categoriesService;
    private final List<Category> categories;

    public PostCategoriesRequest(GcmRegistration gcmRegistration, Categories categoriesService, List<Category> categories) {
        super(null, gcmRegistration);
        Preconditions.checkNotNull(categoriesService, "Categories service cannot be null.");
        Preconditions.checkNotNull(categories, "Categories list cannot be null.");

        this.categoriesService = categoriesService;
        this.categories = categories;
    }

    @Override protected CategoriesBody createBody() {
        return new CategoriesBody();
    }

    @Override protected void onAddPostData(CategoriesBody body) {
        final List<CategoryEntity> categoryEntities = new ArrayList<>();
        for (Category category : categories) {
            categoryEntities.add(category.toEntity());
        }
        body.setCategories(categoryEntities);
    }

    @Override protected boolean isPostDataEmpty(CategoriesBody body) {
        return body.getCategories().isEmpty();
    }

    @Override protected void performRequest(CategoriesBody body) throws Exception {
        categoriesService.save(body);
    }
}
