package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;
import com.code44.finance.utils.AccountsUtils;
import com.code44.finance.utils.NotifyUtils;

public class AccountsService extends AbstractItemService
{
    public static final String EXTRA_CURRENCY_ID = AccountsService.class.getName() + ".EXTRA_CURRENCY_ID";
    public static final String EXTRA_TYPE_RES_NAME = AccountsService.class.getName() + ".EXTRA_TYPE_RES_NAME";
    public static final String EXTRA_TITLE = AccountsService.class.getName() + ".EXTRA_TITLE";
    public static final String EXTRA_NOTE = AccountsService.class.getName() + ".EXTRA_NOTE";
    public static final String EXTRA_BALANCE = AccountsService.class.getName() + ".EXTRA_BALANCE";
    public static final String EXTRA_OVERDRAFT = AccountsService.class.getName() + ".EXTRA_OVERDRAFT";
    public static final String EXTRA_SHOW_IN_TOTALS = AccountsService.class.getName() + ".EXTRA_SHOW_IN_TOTALS";
    public static final String EXTRA_SHOW_IN_SELECTION = AccountsService.class.getName() + ".EXTRA_SHOW_IN_SELECTION";

    @Override
    protected void notifyOnItemUpdated()
    {
        NotifyUtils.onAccountUpdated(this);
    }

    @Override
    protected void prepareValues(ContentValues outValues, Intent intent)
    {
        // Get values
        final long currencyId = intent.getLongExtra(EXTRA_CURRENCY_ID, 0);
        final String typeResName = intent.getStringExtra(EXTRA_TYPE_RES_NAME);
        final String title = intent.getStringExtra(EXTRA_TITLE);
        final String note = intent.getStringExtra(EXTRA_NOTE);
        final double balance = intent.getDoubleExtra(EXTRA_BALANCE, 0);
        final double overdraft = intent.getDoubleExtra(EXTRA_OVERDRAFT, 0);
        final boolean showInTotals = intent.getBooleanExtra(EXTRA_SHOW_IN_TOTALS, true);
        final boolean showInSelection = intent.getBooleanExtra(EXTRA_SHOW_IN_SELECTION, true);

        // Prepare
        AccountsUtils.prepareValues(outValues, currencyId, typeResName, title, note, balance, overdraft, showInTotals, showInSelection);
    }

    @Override
    protected Uri getUriForItems()
    {
        return AccountsProvider.uriAccounts(this);
    }

    @Override
    protected String getItemTable()
    {
        return Tables.Accounts.TABLE_NAME;
    }
}