package com.code44.finance.ui.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.DrawerActivity;
import com.code44.finance.utils.analytics.Analytics;

public class CategoriesReportActivity extends DrawerActivity {
    public static Intent makeIntent(Context context) {
        return makeIntent(context, CategoriesReportActivity.class);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, CategoriesReportFragment.newInstance()).commit();
        }
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return NavigationAdapter.NavigationScreen.Reports;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoriesReport;
    }
}
