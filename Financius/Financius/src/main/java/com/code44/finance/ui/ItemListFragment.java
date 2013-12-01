package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class ItemListFragment extends AbstractFragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
{
    public static final String RESULT_EXTRA_ITEM_ID = ItemListFragment.class.getName() + ".RESULT_EXTRA_ITEM_ID";
    public static final String RESULT_EXTRA_ITEM_IDS = ItemListFragment.class.getName() + ".RESULT_EXTRA_ITEM_IDS";
    // -----------------------------------------------------------------------------------------------------------------
    public static final int SELECTION_TYPE_NONE = 0;
    public static final int SELECTION_TYPE_SINGLE = 1;
    public static final int SELECTION_TYPE_MULTI = 2;
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String ARG_SELECTION_TYPE = "ARG_SELECTION_TYPE";
    protected static final String ARG_ITEM_IDS = "ARG_ITEM_IDS";
    protected static final String ARG_IS_OPEN_DRAWER_LAYOUT = "ARG_IS_OPEN_DRAWER_LAYOUT";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String STATE_SELECTED_POSITIONS = "STATE_SELECTED_POSITIONS";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final int LOADER_ITEMS = 1468;
    // -----------------------------------------------------------------------------------------------------------------
    protected ListView list_V;
    // -----------------------------------------------------------------------------------------------------------------
    protected AbstractCursorAdapter adapter;
    protected int selectionType;

    public static Bundle makeArgs(int selectionType, long[] itemIDs)
    {
        return makeArgs(selectionType, itemIDs, false);
    }

    public static Bundle makeArgs(int selectionType, long[] itemIDs, boolean isOpenDrawerLayout)
    {
        final Bundle args = new Bundle();
        args.putInt(ARG_SELECTION_TYPE, selectionType);
        args.putLongArray(ARG_ITEM_IDS, itemIDs);
        args.putBoolean(ARG_IS_OPEN_DRAWER_LAYOUT, isOpenDrawerLayout);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get arguments
        final Bundle args = getArguments();
        selectionType = args != null ? args.getInt(ARG_SELECTION_TYPE, SELECTION_TYPE_NONE) : SELECTION_TYPE_NONE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_items_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        list_V = (ListView) view.findViewById(R.id.list_V);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        if (selectionType == SELECTION_TYPE_NONE)
        {
            final View create_V = LayoutInflater.from(getActivity()).inflate(R.layout.li_create_new, list_V, false);
            list_V.addFooterView(create_V);
        }
        adapter = createAdapter(getActivity());
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
        if (getArguments().getBoolean(ARG_IS_OPEN_DRAWER_LAYOUT, false))
        {
            final int paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.dynamic_margin_drawer_narrow_horizontal);
            list_V.setPadding(paddingHorizontal, list_V.getPaddingTop(), paddingHorizontal, list_V.getPaddingBottom());
        }
        if (selectionType == SELECTION_TYPE_MULTI)
        {
            list_V.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            if (savedInstanceState != null)
            {
                final ArrayList<Integer> selectedPositions = savedInstanceState.getIntegerArrayList(STATE_SELECTED_POSITIONS);
                list_V.setTag(selectedPositions);
            }
            else
            {
                final long[] selectedIDs = getArguments().getLongArray(ARG_ITEM_IDS);
                list_V.setTag(selectedIDs);
            }
        }

        // Loader
        getLoaderManager().initLoader(LOADER_ITEMS, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        if (selectionType == SELECTION_TYPE_MULTI)
        {
            final ArrayList<Integer> selectedPositions = new ArrayList<Integer>();
            final SparseBooleanArray listPositions = list_V.getCheckedItemPositions();
            if (listPositions != null)
            {
                for (int i = 0; i < listPositions.size(); i++)
                {
                    if (listPositions.get(listPositions.keyAt(i)))
                        selectedPositions.add(listPositions.keyAt(i));
                }
            }
            outState.putIntegerArrayList(STATE_SELECTED_POSITIONS, selectedPositions);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.items_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_create:
                startItemCreate(getActivity(), item.getActionView());
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                bindItems(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ITEMS:
                bindItems(null);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        switch (selectionType)
        {
            case SELECTION_TYPE_NONE:
                if (position == adapterView.getCount() - 1)
                    startItemCreate(getActivity(), view);
                else
                    startItemDetails(getActivity(), id, position, adapter, adapter.getCursor(), view);
                break;

            case SELECTION_TYPE_SINGLE:
                // Prepare extras
                final Bundle extras = new Bundle();
                onItemSelected(id, adapter, adapter.getCursor(), extras);

                Intent data = new Intent();
                data.putExtra(RESULT_EXTRA_ITEM_ID, id);
                data.putExtras(extras);

                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
                break;

            case SELECTION_TYPE_MULTI:
                adapter.setSelectedIDs(list_V.getCheckedItemIds());
                break;
        }
    }

    protected abstract AbstractCursorAdapter createAdapter(Context context);

    protected abstract Loader<Cursor> createItemsLoader();

    /**
     * Called when item id along with extras should be returned to another activity. If only item id is necessary, you don't need to do anything. Just extra values should be put in outExtras.
     *
     * @param itemId    Id of selected item. You don't need to put it to extras. This will be done automatically.
     * @param adapter   Adapter for convenience.
     * @param c         Cursor.
     * @param outExtras Put all additional data in here.
     */
    protected abstract void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras);

    /**
     * Called when you should start item detail activity
     *
     * @param context  Context.
     * @param itemId   Id of selected item.
     * @param position Selected position.
     * @param adapter  Adapter for convenience.
     * @param c        Cursor.
     * @param view
     */
    protected abstract void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c, View view);

    /**
     * Start item create activity here.
     */
    protected abstract void startItemCreate(Context context, View view);

    public long[] getSelectedItemIDs()
    {
        return list_V.getCheckedItemIds();
    }

    protected void bindItems(Cursor c)
    {
        boolean needUpdateSelectedIDs = adapter.getCount() == 0 && selectionType == SELECTION_TYPE_MULTI;
        adapter.swapCursor(c);

        if (needUpdateSelectedIDs && list_V.getTag() != null)
        {
            final Object tag = list_V.getTag();
            if (tag instanceof ArrayList)
            {
                //noinspection unchecked
                final ArrayList<Integer> selectedPositions = (ArrayList<Integer>) tag;
                list_V.setTag(selectedPositions);
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < selectedPositions.size(); i++)
                    list_V.setItemChecked(selectedPositions.get(i), true);
            }
            else if (tag instanceof long[])
            {
                final long[] selectedIDs = (long[]) tag;
                final Set<Long> selectedIDsSet = new HashSet<Long>();
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < selectedIDs.length; i++)
                    selectedIDsSet.add(selectedIDs[i]);

                long itemId;
                for (int i = 0; i < adapter.getCount(); i++)
                {
                    itemId = list_V.getItemIdAtPosition(i);
                    if (selectedIDsSet.contains(itemId))
                    {
                        selectedIDsSet.remove(itemId);
                        list_V.setItemChecked(i, true);

                        if (selectedIDsSet.size() == 0)
                            break;
                    }
                }
            }

            adapter.setSelectedIDs(list_V.getCheckedItemIds());
        }
    }
}