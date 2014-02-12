package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.ui.ItemActivity;
import com.code44.finance.ui.ItemFragment;

public class CategoryItemActivity extends ItemActivity
{
    private static final String EXTRA_CATEGORY_TYPE = CategoryItemActivity.class.getName() + ".EXTRA_CATEGORY_TYPE";
    private static final String EXTRA_QUERY = CategoryItemActivity.class.getName() + ".EXTRA_QUERY";
    // -----------------------------------------------------------------------------------------------------------------
    private int categoryType;
    private String query;

    public static void startItem(Context context, long itemId, int categoryType, String query, View expandFrom)
    {
        Intent intent = makeIntent(context, CategoryItemActivity.class, itemId);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        intent.putExtra(EXTRA_QUERY, query);
        start(context, intent, expandFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Get extras
        final Intent extras = getIntent();
        categoryType = extras.getIntExtra(EXTRA_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);
        query = extras.getStringExtra(EXTRA_QUERY);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected ItemFragment createItemFragment(long itemId)
    {
        return CategoryItemFragment.newInstance(itemId);
    }

    protected String getActivityTitle()
    {
        return getString(R.string.category);
    }
}