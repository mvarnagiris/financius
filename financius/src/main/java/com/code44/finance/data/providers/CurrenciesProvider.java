package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.utils.MoneyFormatter;

import java.util.Map;

public class CurrenciesProvider extends BaseModelProvider {
    public static Uri uriCurrencies() {
        return uriModels(CurrenciesProvider.class, Tables.Currencies.TABLE_NAME);
    }

    public static Uri uriCurrency(long currencyId) {
        return uriModel(CurrenciesProvider.class, Tables.Currencies.TABLE_NAME, currencyId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Currencies.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable();
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onAfterUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterUpdateItems(uri, values, selection, selectionArgs, extras);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, itemState, extras);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions()};
    }
}
