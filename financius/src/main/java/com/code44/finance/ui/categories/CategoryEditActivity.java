package com.code44.finance.ui.categories;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivity;
import com.code44.finance.ui.ModelFragment;

public class CategoryEditActivity extends ModelEditActivity {
    public static void start(Context context, String categoryServerId) {
        start(context, makeIntent(context, CategoryEditActivity.class, categoryServerId));
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.category;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return CategoryEditFragment.newInstance(modelServerId);
    }
}