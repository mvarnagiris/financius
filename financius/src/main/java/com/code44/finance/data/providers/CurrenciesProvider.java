package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;
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
    protected void onBeforeInsertItem(Uri uri, ContentValues values, Map<String, Object> outExtras) {
        super.onBeforeInsertItem(uri, values, outExtras);
        makeSureThereIsOnlyOneDefaultCategory(values);
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, extras);
        Currency.updateDefaultCurrency(database);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeUpdateItems(uri, values, selection, selectionArgs, outExtras);

        if (values.containsKey(Tables.Currencies.CODE.getName())) {
            throw new IllegalArgumentException("Cannot update " + Tables.Currencies.CODE.getName() + " use insert or bulk insert.");
        }

        if (values.containsKey(Tables.Currencies.IS_DEFAULT.getName())) {
            throw new IllegalArgumentException("Cannot update " + Tables.Currencies.IS_DEFAULT.getName() + " use insert or bulk insert.");
        }
    }

    @Override
    protected void onAfterUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterUpdateItems(uri, values, selection, selectionArgs, extras);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, itemState, outExtras);

        final List<Long> affectedIds = new ArrayList<>();

        final Query query = Query.get().projectionId(Tables.Currencies.ID).projection(Tables.Currencies.IS_DEFAULT.getName());
        if (!TextUtils.isEmpty(selection)) {
            query.selection(selection);
        }
        if (selectionArgs != null && selectionArgs.length > 0) {
            query.args(selectionArgs);
        }

        final Cursor cursor = query.asCursor(database, Tables.Currencies.TABLE_NAME);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int iId = cursor.getColumnIndex(Tables.Currencies.ID.getName());
                    int iIsDefault = cursor.getColumnIndex(Tables.Currencies.IS_DEFAULT.getName());
                    if (cursor.getInt(iIsDefault) != 0) {
                        throw new IllegalArgumentException("Cannot delete default currency.");
                    }
                    affectedIds.add(cursor.getLong(iId));
                } while (cursor.moveToNext());
            }
        } finally {
            IOUtils.closeQuietly(cursor);
        }

        outExtras.put("affectedIds", affectedIds);
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, itemState, extras);
        MoneyFormatter.invalidateCache();

        //noinspection unchecked
        final List<Long> affectedIds = (List<Long>) extras.get("affectedIds");
        if (affectedIds.size() > 0) {
            final Query query = Query.get();
            query.selectionInClause(Tables.Accounts.CURRENCY_ID.getName(), affectedIds.size());
            for (Long currencyId : affectedIds) {
                query.args(String.valueOf(currencyId));
            }

            Uri accountsUri = AccountsProvider.uriAccounts();
            switch (itemState) {
                case NORMAL:
                    accountsUri = ProviderUtils.withQueryParameter(accountsUri, ProviderUtils.QueryParameterKey.DELETE_MODE, "undo");
                    break;

                case DELETED_UNDO:
                    accountsUri = ProviderUtils.withQueryParameter(accountsUri, ProviderUtils.QueryParameterKey.DELETE_MODE, "delete");
                    break;

                case DELETED:
                    accountsUri = ProviderUtils.withQueryParameter(accountsUri, ProviderUtils.QueryParameterKey.DELETE_MODE, "commit");
                    break;
            }

            //noinspection ConstantConditions
            getContext().getContentResolver().delete(accountsUri, query.getSelection(), query.getSelectionArgs());
        }

    }

    @Override
    protected void onBeforeBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> outExtras) {
        super.onBeforeBulkInsertItems(uri, valuesArray, outExtras);

        for (ContentValues values : valuesArray) {
            makeSureThereIsOnlyOneDefaultCategory(values);
        }
    }

    @Override
    protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        Currency.updateDefaultCurrency(database);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions()};
    }

    private void makeSureThereIsOnlyOneDefaultCategory(ContentValues values) {
        //noinspection ConstantConditions
        boolean isDefault = values.getAsBoolean(Tables.Currencies.IS_DEFAULT.getName());
        //noinspection ConstantConditions
        long currencyId = values.containsKey(Tables.Currencies.ID.getName()) ? values.getAsLong(Tables.Currencies.ID.getName()) : 0;
        if (isDefault && currencyId != Currency.getDefault().getId()) {
            ContentValues newValues = new ContentValues();
            newValues.put(Tables.Currencies.EXCHANGE_RATE.getName(), 1.0);
            newValues.put(Tables.Currencies.IS_DEFAULT.getName(), false);
            newValues.put(Tables.Currencies.SYNC_STATE.getName(), BaseModel.SyncState.LOCAL_CHANGES.asInt());

            database.update(Tables.Currencies.TABLE_NAME, newValues, null, null);
        }
    }
}
