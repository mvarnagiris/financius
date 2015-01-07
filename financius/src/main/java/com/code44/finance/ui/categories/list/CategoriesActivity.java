package com.code44.finance.ui.categories.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

public class CategoriesActivity extends BaseActivity {
    public static void start(Context context) {
        final Intent intent = makeIntentForActivity(context, CategoriesActivity.class);
        CategoriesActivityPresenter.addViewExtras(intent);
        startActivity(context, intent);
    }

    public static void startSelect(Activity activity, int requestCode, TransactionType transactionType) {
        final Intent intent = makeIntentForActivity(activity, CategoriesActivity.class);
        CategoriesActivityPresenter.addSelectExtras(intent);
        CategoriesActivityPresenter.addExtras(intent, transactionType);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_categories);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CategoriesActivityPresenter();
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoryList;
    }
}
