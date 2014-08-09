package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.R;
import com.code44.finance.ui.ModelActivity;
import com.code44.finance.ui.ModelFragment;

public class CategoryActivity extends ModelActivity {
    public static void start(Context context, String categoryServerId) {
        final Intent intent = makeIntent(context, CategoryActivity.class, categoryServerId);
        start(context, intent);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.category;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return CategoryFragment.newInstance(modelServerId);
    }
}
