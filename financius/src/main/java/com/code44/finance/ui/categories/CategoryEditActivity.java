package com.code44.finance.ui.categories;

import android.content.Context;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivity;
import com.code44.finance.ui.ModelFragment;

public class CategoryEditActivity extends ModelEditActivity {
    public static void start(Context context, View expandFrom, long categoryId) {
        start(context, makeIntent(context, CategoryEditActivity.class, categoryId), expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.category;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return CategoryEditFragment.newInstance(modelId);
    }
}
