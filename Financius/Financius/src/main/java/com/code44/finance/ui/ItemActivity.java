package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public abstract class ItemActivity extends BaseActivity
{
    protected static final String EXTRA_ITEM_ID = ItemEditActivity.class.getName() + ".EXTRA_ITEM_ID";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String FRAGMENT_ITEM = ItemActivity.class.getName() + ".FRAGMENT_ITEM";
    // -----------------------------------------------------------------------------------------------------------------
    protected ItemFragment item_F;
    // -----------------------------------------------------------------------------------------------------------------
    protected long itemId;

    /**
     * Use this when creating intent for subclasses.
     *
     * @param context Context.
     * @param cls     Class of items activity.
     * @param itemId  Id of the item.
     * @return Created intent with required extras.
     */
    protected static Intent makeIntent(Context context, Class cls, long itemId)
    {
        Intent intent = new Intent(context, cls);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final int containerId = inflateView();

        // Setup actionbar
        setActionBarTitle(getActivityTitle());

        // Get extras
        final Intent extras = getIntent();
        itemId = extras.getLongExtra(EXTRA_ITEM_ID, 0);

        // Setup fragment
        item_F = (ItemFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_ITEM);
        if (item_F == null)
        {
            item_F = createItemFragment(itemId);
            getSupportFragmentManager().beginTransaction().replace(containerId, item_F, FRAGMENT_ITEM).commit();
        }
    }

    /**
     * Create new instance of item fragment.
     *
     * @param itemId This is required by {@link ItemFragment}.
     * @return New instance of ItemFragment.
     */
    protected abstract ItemFragment createItemFragment(long itemId);

    /**
     * @return Title of the activity.
     */
    protected abstract String getActivityTitle();

    /**
     * Inflate view here and return ID of container where to put {@link ItemEditFragment}.
     *
     * @return ID of container where to put {@link ItemEditFragment}.
     */
    protected int inflateView()
    {
        return android.R.id.content;
    }
}