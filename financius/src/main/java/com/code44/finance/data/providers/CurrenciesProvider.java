package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.SyncState;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.MoneyFormatter;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class CurrenciesProvider extends BaseModelProvider {
    @Inject Currency defaultCurrency;

    public static Uri uriCurrencies() {
        return uriModels(CurrenciesProvider.class, Tables.Currencies.TABLE_NAME);
    }

    public static Uri uriCurrency(String currencyServerId) {
        return uriModel(CurrenciesProvider.class, Tables.Currencies.TABLE_NAME, currencyServerId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Currencies.TABLE_NAME;
    }

    @Override
    protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Currencies.ID;
    }

    @Override
    protected void onBeforeInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> outExtras) {
        super.onBeforeInsertItem(uri, values, serverId, outExtras);
        makeSureThereIsOnlyOneDefaultCurrency(values);
    }

    @Override
    protected void onAfterInsertItem(Uri uri, ContentValues values, String serverId, Map<String, Object> extras) {
        super.onAfterInsertItem(uri, values, serverId, extras);
        Currency.updateDefaultCurrency(getDatabase(), defaultCurrency);
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
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, modelState, outExtras);

        final List<String> affectedIds = getIdList(getIdColumn(), selection, selectionArgs);
        if (modelState.equals(ModelState.DELETED_UNDO) && affectedIds.contains(defaultCurrency.getId())) {
            throw new IllegalArgumentException("Cannot delete default currency.");
        }
        outExtras.put("affectedIds", affectedIds);
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, modelState, extras);
        MoneyFormatter.invalidateCache();

        //noinspection unchecked
        final List<String> affectedIds = (List<String>) extras.get("affectedIds");
        if (affectedIds.size() > 0) {
            final Query query = Query.create()
                    .selectionInClause(Tables.Accounts.CURRENCY_ID.getName(), affectedIds);

            final Uri accountsUri = uriForDeleteFromItemState(AccountsProvider.uriAccounts(), modelState);
            DataStore.delete()
                    .selection(query.getSelection(), query.getSelectionArgs())
                    .from(getContext(), accountsUri);
        }
    }

    @Override
    protected void onAfterBulkInsertItems(Uri uri, ContentValues[] valuesArray, Map<String, Object> extras) {
        super.onAfterBulkInsertItems(uri, valuesArray, extras);
        Currency.updateDefaultCurrency(getDatabase(), defaultCurrency);
        MoneyFormatter.invalidateCache();
    }

    @Override
    protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{AccountsProvider.uriAccounts(), TransactionsProvider.uriTransactions()};
    }

    private void makeSureThereIsOnlyOneDefaultCurrency(ContentValues values) {
        boolean isDefault = values.getAsBoolean(Tables.Currencies.IS_DEFAULT.getName());
        final String currencyId = values.getAsString(Tables.Currencies.ID.getName());
        if (isDefault && !currencyId.equals(defaultCurrency.getId())) {
            ContentValues newValues = new ContentValues();
            newValues.put(Tables.Currencies.EXCHANGE_RATE.getName(), 1.0);
            newValues.put(Tables.Currencies.IS_DEFAULT.getName(), false);
            newValues.put(Tables.Currencies.SYNC_STATE.getName(), SyncState.LOCAL_CHANGES.asInt());

            getDatabase().update(Tables.Currencies.TABLE_NAME, newValues, null, null);
            getContext().startService(new Intent(getContext(), StartupService.class));
        }
    }
}
