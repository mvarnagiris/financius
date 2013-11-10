package com.code44.finance.ui.categories;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.ui.ItemListActivity;
import com.code44.finance.ui.ItemListFragment;

@SuppressWarnings("ConstantConditions")
public class CategoryListActivity extends ItemListActivity implements ActionBar.OnNavigationListener
{
    private static final String EXTRA_CATEGORY_TYPE = CategoryListActivity.class.getName() + ".EXTRA_CATEGORY_TYPE";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String STATE_SELECTED_ITEM = "STATE_SELECTED_ITEM";
    // -----------------------------------------------------------------------------------------------------------------
    private int categoryType;

    public static void startList(Context context)
    {
        Intent intent = makeIntent(context, CategoryListActivity.class);
        context.startActivity(intent);
    }

    public static void startListSelection(Context context, Fragment fragment, int requestCode, int categoryType)
    {
        final Intent intent = makeIntent(context, CategoryListActivity.class);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        startForSelect(fragment, intent, requestCode);
    }

    public static void startListMultiSelection(Context context, Fragment fragment, int requestCode, int categoryType, long[] itemIDs)
    {
        final Intent intent = makeIntent(context, CategoryListActivity.class);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        startForMultiSelect(fragment, intent, requestCode, itemIDs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Get extras
        final Intent extras = getIntent();
        categoryType = extras.getIntExtra(EXTRA_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);

        super.onCreate(savedInstanceState);

        // Setup ActionBar
        if (selectionType == ItemListFragment.SELECTION_TYPE_NONE)
        {
            final ActionBar actionBar = getActionBar();
            setActionBarTitle(R.string.categories);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            actionBar.setListNavigationCallbacks(new ItemListAdapter(this), this);
            if (savedInstanceState != null)
                actionBar.setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_ITEM, 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_ITEM, getActionBar().getSelectedNavigationIndex());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        //noinspection ConstantConditions
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id)
    {
        if (list_F != null)
            ((CategoryListFragment) list_F).setCategoryType((int) id);
        return true;
    }

    @Override
    protected ItemListFragment createListFragment(int selectionType, long[] itemIDs)
    {
        return CategoryListFragment.newInstance(selectionType, itemIDs, categoryType);
    }

    @Override
    protected String getActivityTitle()
    {
        return getString(R.string.categories);
    }

    private static class ItemListAdapter extends BaseAdapter
    {
        private Context context;

        private ItemListAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            switch (position)
            {
                case 0:
                    return Tables.Categories.Type.EXPENSE;

                case 1:
                    return Tables.Categories.Type.INCOME;
            }
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup)
        {
            if (view == null)
            {
                view = LayoutInflater.from(context).inflate(R.layout.li_ab_category_type, viewGroup, false);
                view.setMinimumHeight(0);
                view.setPadding(0, 0, 0, 0);
            }

            ((TextView) view).setText(position == 0 ? context.getString(R.string.expense) : context.getString(R.string.income));

            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
                convertView = LayoutInflater.from(context).inflate(R.layout.li_ab_category_type, parent, false);

            return super.getDropDownView(position, convertView, parent);
        }
    }
}