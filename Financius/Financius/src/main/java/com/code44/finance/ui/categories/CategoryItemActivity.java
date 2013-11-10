package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.Loader;
import com.code44.finance.R;
import com.code44.finance.adapters.ItemFragmentAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.ui.ItemPagerActivity;

public class CategoryItemActivity extends ItemPagerActivity
{
    private static final String EXTRA_CATEGORY_TYPE = CategoryItemActivity.class.getName() + ".EXTRA_CATEGORY_TYPE";
    private static final String EXTRA_QUERY = CategoryItemActivity.class.getName() + ".EXTRA_QUERY";
    // -----------------------------------------------------------------------------------------------------------------
    private int categoryType;
    private String query;

    public static void startItem(Context context, int position, int categoryType, String query)
    {
        Intent intent = makeIntent(context, CategoryItemActivity.class, position);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        intent.putExtra(EXTRA_QUERY, query);
        context.startActivity(intent);
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

    protected String getActivityTitle()
    {
        return getString(R.string.category);
    }

    @Override
    protected ItemFragmentAdapter createAdapter(Context context, FragmentManager fm)
    {
        return new ItemFragmentAdapter(context, fm, CategoryItemFragment.class);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return CategoryListFragment.createItemsLoader(this, categoryType, query);
    }
}