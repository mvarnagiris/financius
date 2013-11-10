package com.code44.finance.ui.transactions;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import com.code44.finance.R;
import com.code44.finance.adapters.AbstractCursorAdapter;
import com.code44.finance.adapters.AbstractSectionedCursorAdapter;
import com.code44.finance.adapters.TransactionsAdapter;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.ui.MainActivity;
import com.code44.finance.utils.FilterHelper;
import de.greenrobot.event.EventBus;

public class TransactionListFragment extends ItemListFragment implements MainActivity.NavigationContentFragment
{
    public static TransactionListFragment newInstance(int selectionType)
    {
        final TransactionListFragment f = new TransactionListFragment();
        f.setArguments(makeArgs(selectionType, null));
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Register events
        EventBus.getDefault().register(this, FilterHelper.FilterChangedEvent.class);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // Unregister events
        EventBus.getDefault().unregister(this, FilterHelper.FilterChangedEvent.class);
    }

    public static Loader<Cursor> createItemsLoader(Context context)
    {
        final Uri uri = TransactionsProvider.uriTransactions(context);
        final String[] projection = new String[]
                {
                        Tables.Transactions.T_ID, Tables.Transactions.DATE, Tables.Transactions.AMOUNT, Tables.Transactions.NOTE, Tables.Transactions.STATE, Tables.Transactions.EXCHANGE_RATE,
                        Tables.Transactions.ACCOUNT_FROM_ID, Tables.Accounts.AccountFrom.S_TITLE, Tables.Accounts.AccountFrom.S_CURRENCY_ID, Tables.Currencies.CurrencyFrom.S_EXCHANGE_RATE,
                        Tables.Transactions.ACCOUNT_TO_ID, Tables.Accounts.AccountTo.S_TITLE, Tables.Accounts.AccountTo.S_CURRENCY_ID, Tables.Currencies.CurrencyTo.S_EXCHANGE_RATE,
                        Tables.Transactions.CATEGORY_ID, Tables.Categories.CategoriesChild.S_TITLE, Tables.Categories.CategoriesChild.S_TYPE, Tables.Categories.CategoriesChild.S_COLOR
                };

        final FilterHelper filterHelper = FilterHelper.getDefault(context);
        final long startDate = filterHelper.getPeriodStart();
        final long endDate = filterHelper.getPeriodEnd();

        final String selection = Tables.Transactions.DELETE_STATE + "=?" + (startDate > 0 ? " and " + Tables.Transactions.DATE + " >=?" : "") + (endDate > 0 ? " and " + Tables.Transactions.DATE + " <=?" : "");
        final String[] selectionArgs = new String[1 + (startDate > 0 ? 1 : 0) + (endDate > 0 ? 1 : 0)];
        selectionArgs[0] = String.valueOf(Tables.DeleteState.NONE);
        if (startDate > 0)
            selectionArgs[1] = String.valueOf(startDate);
        if (endDate > 0)
            selectionArgs[1 + (startDate > 0 ? 1 : 0)] = String.valueOf(endDate);
        final String sortOrder = Tables.Transactions.STATE + " desc, " + Tables.Transactions.DATE + " desc";

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public String getTitle()
    {
        return getString(R.string.transactions);
    }

    @Override
    protected AbstractCursorAdapter createAdapter(Context context)
    {
        return new TransactionsAdapter(context);
    }

    @Override
    protected Loader<Cursor> createItemsLoader()
    {
        return createItemsLoader(getActivity());
    }

    @Override
    protected void onItemSelected(long itemId, AbstractCursorAdapter adapter, Cursor c, Bundle outExtras)
    {
        // Ignore. There will be no transaction selection for now.
    }

    @Override
    protected void startItemDetails(Context context, long itemId, int position, AbstractCursorAdapter adapter, Cursor c)
    {
        TransactionItemActivity.startItem(context, ((AbstractSectionedCursorAdapter) adapter).getCursorPosition(position));
    }

    @Override
    protected void startItemCreate(Context context)
    {
        TransactionEditActivity.startItemEdit(context, 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FilterHelper.FilterChangedEvent event)
    {
        getLoaderManager().restartLoader(LOADER_ITEMS, null, this);
    }
}