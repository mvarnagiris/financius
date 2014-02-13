package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.ItemFragment;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrencyHelper;

public class CategoryItemFragment extends ItemFragment
{
    private static final int LOADER_PARENT_CATEGORY = 1;
    private static final int LOADER_TRANSACTIONS = 2;
    // -----------------------------------------------------------------------------------------------------------------
    private ImageView color_IV;
    private TextView mainTitle_TV;
    private View subContainer_V;
    private TextView subTitle_TV;
    private TextView lastUsed_TV;
    private TextView timesUsed_TV;
    private TextView avgAmount_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private long parentId;
    private int categoryType;

    public static CategoryItemFragment newInstance(long itemId)
    {
        final CategoryItemFragment f = new CategoryItemFragment();
        f.setArguments(makeArgs(itemId));
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        color_IV = (ImageView) view.findViewById(R.id.color_IV);
        mainTitle_TV = (TextView) view.findViewById(R.id.mainTitle_TV);
        subContainer_V = view.findViewById(R.id.subContainer_V);
        subTitle_TV = (TextView) view.findViewById(R.id.subTitle_TV);
        lastUsed_TV = (TextView) view.findViewById(R.id.lastUsed_TV);
        timesUsed_TV = (TextView) view.findViewById(R.id.timesUsed_TV);
        avgAmount_TV = (TextView) view.findViewById(R.id.avgAmount_TV);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Loader
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_PARENT_CATEGORY:
            {
                Uri uri = CategoriesProvider.uriCategory(parentId);
                String[] projection = new String[]{Tables.Categories.TITLE, Tables.Categories.LEVEL};

                return new CursorLoader(getActivity(), uri, projection, null, null, null);
            }

            case LOADER_TRANSACTIONS:
            {
                Uri uri = TransactionsProvider.uriTransactions();
                String[] projection = new String[]
                        {
                                "max(" + Tables.Transactions.DATE + ")",
                                "count(" + Tables.Transactions.T_ID + ")",
                                "avg(" + Tables.Transactions.AMOUNT + " * case " + Tables.Categories.CategoriesChild.T_TYPE + " when " + Tables.Categories.Type.EXPENSE + " then " + Tables.Currencies.CurrencyFrom.T_EXCHANGE_RATE + " else " + Tables.Currencies.CurrencyTo.T_EXCHANGE_RATE + " end)"};
                String selection = Tables.Transactions.CATEGORY_ID + "=? and " + Tables.Transactions.STATE + "=? and " + Tables.Transactions.DELETE_STATE + "=? and " + Tables.Transactions.SHOW_IN_TOTALS + "=?";
                String[] selectionArgs = new String[]{String.valueOf(itemId), String.valueOf(Tables.Transactions.State.CONFIRMED), String.valueOf(Tables.DeleteState.NONE), "1"};

                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
            }
        }
        return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_PARENT_CATEGORY:
                bindParent(cursor);
                break;

            case LOADER_TRANSACTIONS:
                bindTransactions(cursor);
                break;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    protected void startItemEdit(Context context, long itemId, View expandFrom)
    {
        CategoryEditActivity.startItemEdit(context, itemId, categoryType);
    }

    @Override
    protected boolean onDeleteItem(Context context, long[] itemIds)
    {
        API.deleteItems(CategoriesProvider.uriCategories(), itemIds);
        return true;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = CategoriesProvider.uriCategory(itemId);
        String[] projection = new String[]{Tables.Categories.PARENT_ID, Tables.Categories.TITLE, Tables.Categories.COLOR, Tables.Categories.TYPE, Tables.Categories.LEVEL};

        return new CursorLoader(getActivity(), uri, projection, null, null, null);
    }

    @Override
    protected void bindItem(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            // Find indexes
            final int iParentId = c.getColumnIndex(Tables.Categories.PARENT_ID);
            final int iTitle = c.getColumnIndex(Tables.Categories.TITLE);
            final int iColor = c.getColumnIndex(Tables.Categories.COLOR);
            final int iLevel = c.getColumnIndex(Tables.Categories.LEVEL);

            // Get values
            parentId = c.getLong(iParentId);
            categoryType = c.getInt(c.getColumnIndex(Tables.Categories.TYPE));
            final String title = c.getString(iTitle);

            // Set values
            if (c.getInt(iLevel) == 1)
            {
                mainTitle_TV.setText(title);
                subContainer_V.setVisibility(View.GONE);
            }
            else
            {
                subTitle_TV.setText(title);
                subContainer_V.setVisibility(View.VISIBLE);
            }
            //noinspection ConstantConditions
            ((GradientDrawable) color_IV.getDrawable()).setColor(c.getInt(iColor));

            // Reload parent
            if (parentId > 0)
                getLoaderManager().restartLoader(LOADER_PARENT_CATEGORY, null, this);
        }
    }

    private void bindParent(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            // Find indexes
            final int iTitle = c.getColumnIndex(Tables.Categories.TITLE);
            final int iLevel = c.getColumnIndex(Tables.Categories.LEVEL);

            if (c.getInt(iLevel) == 1)
                mainTitle_TV.setText(c.getString(iTitle));
        }
    }

    private void bindTransactions(Cursor c)
    {
        if (c != null && c.moveToFirst())
        {
            final long lastUsedDate = c.getLong(0);
            lastUsed_TV.setText(lastUsedDate == 0 ? getString(R.string.never) : DateUtils.getRelativeTimeSpanString(c.getLong(0), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS));
            timesUsed_TV.setText(c.getString(1));
            avgAmount_TV.setText(AmountUtils.formatAmount(CurrencyHelper.get().getMainCurrencyId(), c.getDouble(2)));
        }
    }
}