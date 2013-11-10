package com.code44.finance.ui.reports;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.code44.finance.R;
import com.code44.finance.ui.AbstractActivity;

public class CategoriesReportActivity extends AbstractActivity
{
    public static void startCategoriesReport(Context context)
    {
        Intent intent = new Intent(context, CategoriesReportActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setActionBarTitle(R.string.categories_report);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, CategoriesReportFragment.newInstance()).commit();
    }
}