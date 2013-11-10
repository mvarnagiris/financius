package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import com.code44.finance.R;
import com.code44.finance.adapters.ItemFragmentAdapter;

public abstract class ItemPagerActivity extends AbstractActivity implements LoaderManager.LoaderCallbacks<Cursor>, ViewPager.OnPageChangeListener
{
    protected static final String EXTRA_POSITION = ItemEditActivity.class.getName() + ".EXTRA_POSITION";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String STATE_POSITION = "STATE_POSITION";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final int LOADER_ITEMS = 41774;
    // -----------------------------------------------------------------------------------------------------------------
    protected ViewPager pager_VP;
    // -----------------------------------------------------------------------------------------------------------------
    protected ItemFragmentAdapter adapter;
    protected int position;

    /**
     * Use this when creating intent for subclasses.
     *
     * @param context  Context.
     * @param cls      Class of items activity.
     * @param position position of selected item.
     * @return Created intent with required extras.
     */
    protected static Intent makeIntent(Context context, Class cls, int position)
    {
        Intent intent = new Intent(context, cls);
        intent.putExtra(EXTRA_POSITION, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final int pagerId = inflateView();

        // Setup actionbar
        setActionBarTitle(getActivityTitle());

        // Get extras or restore state
        final Intent extras = getIntent();
        position = savedInstanceState == null ? extras.getIntExtra(EXTRA_POSITION, 0) : savedInstanceState.getInt(STATE_POSITION);

        // Get views
        pager_VP = (ViewPager) findViewById(pagerId);

        // Setup
        adapter = createAdapter(this, getSupportFragmentManager());
        pager_VP.setPageMargin(getResources().getDimensionPixelSize(R.dimen.space_normal));
        pager_VP.setPageMarginDrawable(R.color.bg_window);
        pager_VP.setAdapter(adapter);
        pager_VP.setOnPageChangeListener(this);

        // Loader
        getSupportLoaderManager().initLoader(LOADER_ITEMS, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, position);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_ITEMS:
                return createItemsLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ITEMS:
                final boolean needSetPosition = adapter.getCount() == 0;
                adapter.swapCursor(cursor);
                if (needSetPosition && position < adapter.getCount())
                    pager_VP.setCurrentItem(position, false);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ITEMS:
                adapter.swapCursor(null);
                break;
        }
    }

    /**
     * @return Title of the activity.
     */
    protected abstract String getActivityTitle();

    protected abstract ItemFragmentAdapter createAdapter(Context context, FragmentManager fm);

    protected abstract Loader<Cursor> createItemsLoader();

    @Override
    public void onPageScrolled(int i, float v, int i2)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
        this.position = position;
    }

    @Override
    public void onPageScrollStateChanged(int i)
    {
    }

    /**
     * Inflate view here and return ID of view pager.
     *
     * @return ID of view pager.
     */
    protected int inflateView()
    {
        setContentView(R.layout.activity_pager);
        return R.id.pager_VP;
    }
}