package com.code44.finance.ui.categories.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

public class CategoryEditActivity extends BaseActivity {
    public static void start(Context context, String categoryId) {
        final Intent intent = makeIntentForActivity(context, CategoryEditActivity.class);
        CategoryEditActivityPresenter.addExtras(intent, categoryId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_category_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CategoryEditActivityPresenter(getEventBus());
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoryEdit;
    }
}