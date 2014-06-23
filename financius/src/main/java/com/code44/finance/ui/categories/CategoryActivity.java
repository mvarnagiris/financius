package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class CategoryActivity extends ModelActivity {
    public static void start(Context context, View expandFrom, long categoryId) {
        final Intent intent = makeIntent(context, CategoryActivity.class, categoryId);
        start(context, intent, expandFrom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return !(modelId == Category.EXPENSE_ID || modelId == Category.INCOME_ID || modelId == Category.TRANSFER_ID) && super.onCreateOptionsMenu(menu);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.category;
    }

    @Override
    protected ModelFragment createModelFragment(long modelId) {
        return CategoryFragment.newInstance(modelId);
    }

    @Override
    protected void startEditActivity(long modelId) {
        // TODO Implement
    }
}
