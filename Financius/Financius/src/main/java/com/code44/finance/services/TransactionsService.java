package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.TransactionsProvider;
import com.code44.finance.utils.TransactionsUtils;

public class TransactionsService extends AbstractItemService
{
    public static final String EXTRA_ACCOUNT_FROM_ID = TransactionsService.class.getName() + ".EXTRA_ACCOUNT_FROM_ID";
    public static final String EXTRA_ACCOUNT_TO_ID = TransactionsService.class.getName() + ".EXTRA_ACCOUNT_TO_ID";
    public static final String EXTRA_CATEGORY_ID = TransactionsService.class.getName() + ".EXTRA_CATEGORY_ID";
    public static final String EXTRA_DATE = TransactionsService.class.getName() + ".EXTRA_DATE";
    public static final String EXTRA_AMOUNT = TransactionsService.class.getName() + ".EXTRA_AMOUNT";
    public static final String EXTRA_EXCHANGE_RATE = TransactionsService.class.getName() + ".EXTRA_EXCHANGE_RATE";
    public static final String EXTRA_NOTE = TransactionsService.class.getName() + ".EXTRA_NOTE";
    public static final String EXTRA_STATE = TransactionsService.class.getName() + ".EXTRA_STATE";
    public static final String EXTRA_SHOW_IN_TOTALS = TransactionsService.class.getName() + ".EXTRA_SHOW_IN_TOTALS";

    @Override
    protected void notifyOnItemUpdated()
    {
    }

    @Override
    protected void prepareValues(ContentValues outValues, Intent intent)
    {
        // Get values
        final long accountFromId = intent.getLongExtra(EXTRA_ACCOUNT_FROM_ID, 0);
        final long accountToId = intent.getLongExtra(EXTRA_ACCOUNT_TO_ID, 0);
        final long categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);
        final long date = intent.getLongExtra(EXTRA_DATE, 0);
        final double amount = intent.getDoubleExtra(EXTRA_AMOUNT, 0);
        final double exchangeRate = intent.getDoubleExtra(EXTRA_EXCHANGE_RATE, 1.0);
        final String note = intent.getStringExtra(EXTRA_NOTE);
        final int state = intent.getIntExtra(EXTRA_STATE, Tables.Transactions.State.CONFIRMED);
        final boolean showInTotals = intent.getBooleanExtra(EXTRA_SHOW_IN_TOTALS, true);

        // Prepare
        TransactionsUtils.prepareValues(outValues, accountFromId, accountToId, categoryId, date, amount, exchangeRate, note, state, showInTotals);
    }

    @Override
    protected Uri getUriForItems()
    {
        return TransactionsProvider.uriTransactions();
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Transactions.TABLE_NAME;
    }
}