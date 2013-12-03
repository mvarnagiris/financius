package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;
import com.code44.finance.adapters.CategoriesAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.utils.PrefsHelper;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/**
 * Displays a list of accounts.
 *
 * @author Mantas Varnagiris
 */
public class CategoryListFragment extends ItemListFragment
{
    public static final String RESULT_EXTRA_CATEGORY_TITLE = CategoryListFragment.class.getName() + ".RESULT_EXTRA_CATEGORY_TITLE";
    public static final String RESULT_EXTRA_CATEGORY_TYPE = CategoryListFragment.class.getName() + ".RESULT_EXTRA_CATEGORY_TYPE";
    public static final String RESULT_EXTRA_CATEGORY_COLOR = CategoryListFragment.class.getName() + ".RESULT_EXTRA_CATEGORY_COLOR";
    public static final String RESULT_EXTRA_CATEGORY_LEVEL = CategoryListFragment.class.getName() + ".RESULT_EXTRA_CATEGORY_LEVEL";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String ARG_CATEGORY_TYPE = CategoryListFragment.class.getName() + ".ARG_CATEGORY_TYPE";
    // -----------------------------------------------------------------------------------------------------------------
    private SearchView search_V;
    private View separator_V;
    // -----------------------------------------------------------------------------------------------------------------
    private int categoryType;

    public static CategoryListFragment newInstance(int selectionType, long[] itemIDs, int categoryType)
    {
        final Bundle args = makeArgs(selectionType, itemIDs);
        args.putInt(ARG_CATEGORY_TYPE, categoryType);

        final CategoryListFragment f = new CategoryListFragment();
        f.setArguments(args);
        return f;
    }

    public static String getLoaderSelection(String query)
    {
        if (query != null && query.trim().length() == 0)
            query = null;

        return Tables.Categories.DELETE_STATE + "=?"
                + " and " + Tables.Categories.LEVEL + ">?"
                + " and " + Tables.Categories.TYPE + "=?"
                + (TextUtils.isEmpty(query) ? "" : " and lower(" + Tables.Categories.TITLE + ") glob ?");
    }

    public static String[] getLoaderSelectionArgs(int categoryType, String query)
    {
        if (query != null && query.trim().length() == 0)
            query = null;

        final String[] args = new String[TextUtils.isEmpty(query) ? 3 : 4];
        args[0] = String.valueOf(Tables.DeleteState.NONE);
        args[1] = "0";
        args[2] = String.valueOf(categoryType);
        if (!TextUtils.isEmpty(query))
            args[3] = "*" + query.toLowerCase() + "*";
        return args;
    }

    public static String getLoaderSortOrder()
    {
        return "case " + Tables.Categories.LEVEL + " when 1 then " + Tables.Categories.ORDER + " else " + Tables.Categories.PARENT_ORDER + " end, " + Tables.Categories.LEVEL + ", " + Tables.Categories.ORDER;
    }

    public static Loader<Cursor> createItemsLoader(Context context, int categoryType, String query)
    {
        final Uri uri = CategoriesProvider.uriCategories();
        final String[] projection = new String[]{Tables.Categories.T_ID, Tables.Categories.PARENT_ID, Tables.Categories.LEVEL, Tables.Categories.TITLE,
                Tables.Categories.TYPE, Tables.Categories.COLOR, Tables.Categories.ORIGIN};
        final String selection = getLoaderSelection(query);
        final String[] selectionArgs = getLoaderSelectionArgs(categoryType, query);
        final String sortOrder = getLoaderSortOrder();

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get arguments
        final Bundle args = getArguments();
        // We also check saved instance state, because this type might have changed.
        categoryType = savedInstanceState != null ? savedInstanceState.getInt(ARG_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE) : args.getInt(ARG_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        search_V = (SearchView) view.findViewById(R.id.search_V);
        separator_V = view.findViewById(R.id.separator_V);

        // Setup
        if (!PrefsHelper.getDefault(getActivity()).isFocusCategoriesSearch() && selectionType != SELECTION_TYPE_MULTI)
            toggleSearch();
        else if (selectionType != SELECTION_TYPE_MULTI)
            search_V.requestFocus();
        search_V.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                ((CategoriesAdapter) adapter).setQuery(newText);
                getLoaderManager().restartLoader(LOADER_ITEMS, null, CategoryListFragment.this);
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        ((CategoriesAdapter) adapter).setCategoryType(categoryType);
        SectionController c = new SectionController(list_V, (CategoriesAdapter) adapter);
        ((DragSortListView) list_V).setFloatViewManager(c);
        ((DragSortListView) list_V).setDropListener((DragSortListView.DropListener) adapter);
        list_V.setOnTouchListener(c);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_CATEGORY_TYPE, categoryType);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.category_list, menu);

        if (selectionType == SELECTION_TYPE_MULTI)
            menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_search:
                toggleSearch();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCategoryType(int categoryType)
    {
        if (this.categoryType != categoryType)
        {
            this.categoryType = categoryType;
            ((CategoriesAdapter) adapter).setCategoryType(categoryType);
            getLoaderManager().restartLoader(LOADER_ITEMS, null, this);
        }
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new CategoriesAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        CursorLoader loader = (CursorLoader) createItemsLoader(getActivity(), categoryType, ((CategoriesAdapter) adapter).getQuery());
        loader.setUpdateThrottle(1000);
        return loader;
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        outExtras.putString(RESULT_EXTRA_CATEGORY_TITLE, c.getString(c.getColumnIndex(Tables.Categories.TITLE)));
        outExtras.putInt(RESULT_EXTRA_CATEGORY_TYPE, c.getInt(c.getColumnIndex(Tables.Categories.TYPE)));
        outExtras.putInt(RESULT_EXTRA_CATEGORY_COLOR, c.getInt(c.getColumnIndex(Tables.Categories.COLOR)));
        outExtras.putInt(RESULT_EXTRA_CATEGORY_LEVEL, c.getInt(c.getColumnIndex(Tables.Categories.LEVEL)));
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c, View view)
    {
        CategoryItemActivity.startItem(context, position, c.getInt(c.getColumnIndex(Tables.Categories.TYPE)), ((CategoriesAdapter) adapter).getQuery());
    }

    @Override
    protected void startItemCreate(Context context, View view)
    {
        CategoryEditActivity.startItemEdit(context, 0, categoryType);
    }

    private void toggleSearch()
    {
        if (search_V.getVisibility() == View.VISIBLE)
        {
            search_V.setVisibility(View.GONE);
            separator_V.setVisibility(View.GONE);
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search_V.getWindowToken(), 0);
        }
        else
        {
            search_V.setVisibility(View.VISIBLE);
            separator_V.setVisibility(View.VISIBLE);
            search_V.requestFocus();
        }
    }

    private static class SectionController extends DragSortController
    {
        private final ListView list_V;
        private final CategoriesAdapter adapter;
        private int topSectionPosition;
        private int bottomSectionPosition;

        public SectionController(ListView list_V, CategoriesAdapter adapter)
        {
            super((DragSortListView) list_V, 0, DragSortController.ON_LONG_PRESS, 0);
            setRemoveEnabled(false);
            this.list_V = list_V;
            this.adapter = adapter;
        }

        @Override
        public View onCreateFloatView(int position)
        {
            topSectionPosition = adapter.getTopSectionStart(position);
            bottomSectionPosition = adapter.getBottomSectionStart(position);
            return super.onCreateFloatView(position);
        }

        @Override
        public void onDragFloatView(View floatView, Point floatPoint, Point touchPoint)
        {
            final int first = list_V.getFirstVisiblePosition();
            final int lvDivHeight = list_V.getDividerHeight();

            View bottomDiv = list_V.getChildAt(bottomSectionPosition - first);
            View topDiv = list_V.getChildAt(topSectionPosition - first);

            if (bottomDiv != null)
            {
                // Don't allow floating View to go below section divider
                final int limit = bottomDiv.getTop() - lvDivHeight - floatView.getHeight();
                if (floatPoint.y > limit)
                    floatPoint.y = limit;
            }

            if (topDiv != null)
            {
                // Don't allow floating View to go above section divider
                final int limit = topDiv.getBottom() + lvDivHeight;
                if (floatPoint.y < limit)
                    floatPoint.y = limit;
            }
        }

        @Override
        public void onDestroyFloatView(View floatView)
        {
            //do nothing; block super from crashing
        }

    }
}