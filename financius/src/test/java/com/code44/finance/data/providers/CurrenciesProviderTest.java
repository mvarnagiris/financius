package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.BaseModel;
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

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException_whenValuesContainIsDefault() {
        ContentValues values = new ContentValues();
        values.put(Tables.Currencies.IS_DEFAULT.getName(), "1");

        contentResolver.update(CurrenciesProvider.uriCurrencies(), values, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException_whenValuesContainCode() {
        ContentValues values = new ContentValues();
        values.put(Tables.Currencies.CODE.getName(), "AAA");

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
        final long accountId = insert(AccountsProvider.uriAccounts(), account);

        // Delete currency
        final Uri uri = ProviderUtils.withQueryParameter(CurrenciesProvider.uriCurrencies(), ProviderUtils.QueryParameterKey.DELETE_MODE, "delete");
        contentResolver.delete(uri, Tables.Currencies.ID.getNameWithTable() + "=?", new String[]{String.valueOf(currency.getId())});

        // Assert
        final Query accountQuery = Query.get()
                .projectionId(Tables.Accounts.ID).projection(Tables.Accounts.PROJECTION).projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Accounts.ID.getNameWithTable() + "=?", String.valueOf(accountId));
        final Cursor accountCursor = accountQuery.asCursor(context, AccountsProvider.uriAccounts());
        final Account accountFromDB = Account.from(accountCursor);
        IOUtils.closeQuietly(cursor);
        assertEquals(accountFromDB.getItemState(), BaseModel.ItemState.DELETED_UNDO);
        assertEquals(accountFromDB.getCurrency().getItemState(), BaseModel.ItemState.DELETED_UNDO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_throwsIllegalArgumentException_whenTryingToDeleteDefaultCurrency() {
        contentResolver.delete(ProviderUtils.withQueryParameter(CurrenciesProvider.uriCurrencies(), ProviderUtils.QueryParameterKey.DELETE_MODE, "delete"), null, null);
    }

    @Test
    public void bulkInsert_makesSureThatThereIsOnlyOneDefaultCurrency() {
        final Currency currency = new Currency();
        currency.setCode("AAA");
        currency.setDefault(true);

        contentResolver.bulkInsert(CurrenciesProvider.uriCurrencies(), new ContentValues[]{currency.asContentValues()});

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
    public void bulkInsert_updatesCurrencies_whenCurrenciesWithSameCodeExists() {
        final Currency currency = new Currency();
        currency.setCode("USD");
        currency.setExchangeRate(1.2345);

        contentResolver.bulkInsert(CurrenciesProvider.uriCurrencies(), new ContentValues[]{currency.asContentValues()});

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
}
