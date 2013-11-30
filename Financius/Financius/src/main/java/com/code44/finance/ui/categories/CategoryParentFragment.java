package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;
import com.code44.finance.adapters.CategoryParentsAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.utils.AnimUtils;

@SuppressWarnings("ConstantConditions")
public class CategoryParentFragment extends ItemListFragment
{
    private static final String ARG_CATEGORY_TYPE = "ARG_CATEGORY_TYPE";
    // -----------------------------------------------------------------------------------------------------------------
    private Callbacks callbacks;
    private int categoryType;

    public static CategoryParentFragment newInstance(int categoryType)
    {
        final Bundle args = makeArgs(SELECTION_TYPE_NONE, null);
        args.putInt(ARG_CATEGORY_TYPE, categoryType);

        final CategoryParentFragment f = new CategoryParentFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        // Get args
        final Bundle args = getArguments();
        categoryType = args.getInt(ARG_CATEGORY_TYPE, Tables.Categories.Type.EXPENSE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_category_parent, container, false);
    }

    public long getSelectedId()
    {
        final long selectedId = ((CategoryParentsAdapter) adapter).getSelectedId();
        if (selectedId <= 0)
            AnimUtils.shake(list_V);

        return selectedId;
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new CategoryParentsAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        Uri uri = CategoriesProvider.uriCategories(getActivity());
        String[] projection = null;
        String selection = Tables.Categories.LEVEL + "<=? and " + Tables.Categories.DELETE_STATE + "=? and " + Tables.Categories.TYPE + "=?";
        String[] selectionArgs = new String[]{"1", String.valueOf(Tables.DeleteState.NONE), String.valueOf(categoryType)};
        String sortOrder = Tables.Categories.LEVEL + "," + Tables.Categories.TITLE;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        // Not doing selections
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c)
    {
        ((CategoryParentsAdapter) adapter).setSelectedId(itemId);
        if (callbacks != null)
            callbacks.onParentCategorySelected(itemId);
    }

    @Override
    protected void startItemCreate(Context context, View expandFrom)
    {
        // Ignore this.
    }

    public void setCallbacks(Callbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    public static interface Callbacks
    {
        public void onParentCategorySelected(long id);
    }
}
