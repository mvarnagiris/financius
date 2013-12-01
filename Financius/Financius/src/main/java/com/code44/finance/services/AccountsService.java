package com.code44.finance.services;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.AccountsProvider;

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
        //NotifyUtils.onAccountUpdated(this);
    }

    @Override
    protected void prepareValues(ContentValues outValues, Intent intent)
    {
        //noinspection ConstantConditions
        outValues.putAll((ContentValues) intent.getParcelableExtra(EXTRA_CONTENT_VALUES));
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