package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import com.code44.finance.db.Tables;
import com.code44.finance.ui.ItemEditActivity;
import com.code44.finance.ui.ItemEditFragment;

public class CategoryEditActivity extends ItemEditActivity
{
    private static final String EXTRA_CATEGORY_TYPE = CategoryEditActivity.class.getName() + ".EXTRA_CATEGORY_TYPE";
    private int categoryType;

    public static void startItemEdit(Context context, long itemId, int categoryType)
    {
        Intent intent = makeIntent(context, CategoryEditActivity.class, itemId);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        context.startActivity(intent);
    }

    @Override
    protected ItemEditFragment createItemEditFragment(long itemId)
    {
        return CategoryEditFragment.newInstance(itemId, categoryType);
    }

    @Override
    protected int inflateView()
    {
        // Get extras
        final Intent extras = getIntent();
        categoryType = extras.getIntExtra(EXTRA_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);

        return super.inflateView();
    }
}