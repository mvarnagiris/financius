package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class CategoriesActivity extends ModelListActivity {
    public static void start(Context context, View expandFrom) {
        final Intent intent = makeIntentView(context, CategoriesActivity.class);
        start(context, intent, expandFrom);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.categories;
    }

    @Override
    protected ModelListFragment createModelsFragment(int mode) {
        return CategoriesFragment.newInstance(mode, Category.Type.EXPENSE);
    }
}
