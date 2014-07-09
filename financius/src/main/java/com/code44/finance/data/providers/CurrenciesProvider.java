package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.utils.MoneyFormatter;

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
        makeSureThereIsOnlyOneDefaultCurrency(values);
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, long id, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, id, extras);
        Currency.updateDefaultCurrency(database);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeUpdateItems(uri, values, selection, selectionArgs, outExtras);
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onAfterUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterUpdateItems(uri, values, selection, selectionArgs, extras);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, itemState, outExtras);

        final List<Long> affectedIds = getIdList(Tables.Currencies.TABLE_NAME, selection, selectionArgs);
        if (itemState.equals(BaseModel.ItemState.DELETED_UNDO) && affectedIds.contains(Currency.getDefault().getId())) {
            throw new IllegalArgumentException("Cannot delete default currency.");
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
            final Query query = Query.create()
                    .selectionInClause(Tables.Accounts.CURRENCY_ID.getName(), affectedIds);

            final Uri accountsUri = uriForDeleteFromItemState(AccountsProvider.uriAccounts(), itemState);
            DataStore.delete()
                    .selection(query.getSelection(), query.getSelectionArgs())
                    .from(accountsUri);
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

    private void makeSureThereIsOnlyOneDefaultCurrency(ContentValues values) {
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
