package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.R;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.ui.ModelEditActivity;
import com.code44.finance.ui.ModelFragment;

public class CategoryEditActivity extends ModelEditActivity {
    private static final String EXTRA_CATEGORY_TYPE = "EXTRA_CATEGORY_TYPE";

    private CategoryType categoryType;

    public static void start(Context context, String categoryServerId, CategoryType categoryType) {
        final Intent intent = makeIntent(context, CategoryEditActivity.class, categoryServerId);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        start(context, intent);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.category;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return CategoryEditFragment.newInstance(modelServerId, categoryType);
    }

    @Override protected void readExtras() {
        super.readExtras();
        categoryType = (CategoryType) getIntent().getSerializableExtra(EXTRA_CATEGORY_TYPE);
    }
}