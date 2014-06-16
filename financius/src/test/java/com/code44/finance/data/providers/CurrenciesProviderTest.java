package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.utils.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CurrenciesProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_makesSureThatThereIsOnlyOneDefaultCurrency() {
        final Currency currency = new Currency();
        currency.setCode("AAA");
        currency.setDefault(true);

        insert(CurrenciesProvider.uriCurrencies(), currency);

        final Query query = Query.get()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT + "=?", "1");
        assertQuerySize(CurrenciesProvider.uriCurrencies(), query, 1);

        Cursor cursor = query.asCursor(context, CurrenciesProvider.uriCurrencies());
        final Currency currencyFromDb = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        assertEquals(currency.getCode(), currencyFromDb.getCode());
    }

    @Test
    public void insert_updatesCurrency_whenCurrencyWithSameCodeExists() {
        final Currency currency = new Currency();
        currency.setCode("USD");
        currency.setExchangeRate(1.2345);

        insert(CurrenciesProvider.uriCurrencies(), currency);

        final Query query = Query.get()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?", currency.getCode());
        assertQuerySize(CurrenciesProvider.uriCurrencies(), query, 1);

        Cursor cursor = query.asCursor(context, CurrenciesProvider.uriCurrencies());
        final Currency currencyFromDb = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        assertEquals(currency.getExchangeRate(), currencyFromDb.getExchangeRate(), 0.0001);
    }

    @Test(expected = IllegalAccessException.class)
    public void update_doesNotAllowToChangeDefaultCurrency() {
        ContentValues values = new ContentValues();
        values.put(Tables.Currencies.IS_DEFAULT.getName(), "1");

        contentResolver.update(CurrenciesProvider.uriCurrencies(), values, null, null);
    }

    @Test
    public void delete_setsTheSameItemStateForAccounts() {
        // Get any currency that is not default
        final Query query = Query.get()
                .projectionId(Tables.Currencies.ID).projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT + "=?", "0");
        final Cursor cursor = query.asCursor(context, CurrenciesProvider.uriCurrencies());
        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        // Insert new account for that currency
        final Account account = new Account();
        account.setTitle("a");
        account.setCurrency(currency);
        insert(AccountsProvider.uriAccounts(), account);

        // Delete currency
        Uri uri
        contentResolver.delete(ProviderUtils.withQueryParameter(CurrenciesProvider.uriCurrencies(), ProviderUtils.QueryParameterKey.DELETE_MODE, "delete"), Tables.Currencies.ID.getNameWithTable() + "=?", String.valueOf(currency.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_doesNotAllowToDeleteDefaultCurrency() {
        contentResolver.delete(CurrenciesProvider.uriCurrencies(), null, null);
    }

    @Test
    public void bulkInsert_updatesCurrencies_whenCurrenciesWithSameCodeExists() {
        fail();
    }

    @Test
    public void bulkInsert_makesSureThatThereIsOnlyOneDefaultCurrency() {
        fail();
    }
}
