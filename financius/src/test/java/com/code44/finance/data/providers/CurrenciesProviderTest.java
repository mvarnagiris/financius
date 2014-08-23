package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;

import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.utils.IOUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class CurrenciesProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_leavesOnlyNewDefaultCurrencyAsDefault_whenInsertingNewDefaultCurrency() {
        Cursor cursor = queryDefaultCurrencyCursor();
        assertEquals(1, cursor.getCount());
        assertEquals(Currency.getDefault().getId(), Currency.from(cursor).getId());
        IOUtils.closeQuietly(cursor);

        final Currency currency = insertCurrency(true);
        cursor = queryDefaultCurrencyCursor();
        assertEquals(1, cursor.getCount());
        assertEquals(currency.getId(), Currency.from(cursor).getId());
        assertEquals(currency.getId(), Currency.getDefault().getId());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_updatesCurrency_whenCurrencyWithSameCodeExists() {
        final Currency currency = insertCurrency(false);
        currency.setExchangeRate(1.2345);
        insertCurrency(currency);

        final Cursor cursor = queryCurrencyCursor(currency.getCode());
        assertEquals(1, cursor.getCount());
        assertEquals(currency.getExchangeRate(), Currency.from(cursor).getExchangeRate(), 0.0001);
        IOUtils.closeQuietly(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(CurrenciesProvider.uriCurrencies(), new ContentValues(), null);
    }

    @Test
    public void deleteDelete_setsItemStateDeletedUndoForAccounts() {
        final Currency currency = insertCurrency(false);
        insertAccount(currency);

        deleteCurrency(currency);
        final Cursor cursor = queryAccountsCursor();

        assertEquals(1, cursor.getCount());
        assertEquals(ModelState.DELETED_UNDO, Account.from(cursor).getModelState());
        IOUtils.closeQuietly(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_throwsIllegalArgumentException_whenTryingToDeleteDefaultCurrency() {
        delete("delete", CurrenciesProvider.uriCurrencies(), null);
    }

    private Cursor queryDefaultCurrencyCursor() {
        final Query query = Query.create()
                .projectionLocalId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT + "=?", "1");

        return query(CurrenciesProvider.uriCurrencies(), query);
    }

    private Cursor queryCurrencyCursor(String code) {
        final Query query = Query.create()
                .projectionLocalId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?", code);

        return query(CurrenciesProvider.uriCurrencies(), query);
    }

    private Currency insertCurrency(boolean isDefault) {
        final Currency currency = new Currency();
        currency.setCode("AAA");
        currency.setDefault(isDefault);
        return insertCurrency(currency);
    }

    private Currency insertCurrency(Currency currency) {
        insert(CurrenciesProvider.uriCurrencies(), currency);
        return currency;
    }

    private int deleteCurrency(Currency currency) {
        return delete("delete", CurrenciesProvider.uriCurrencies(), Tables.Currencies.LOCAL_ID + "=?", String.valueOf(currency.getId()));
    }

    private Cursor queryAccountsCursor() {
        final Query query = Query.create()
                .projectionLocalId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .selection(Tables.Accounts.OWNER + "=?", AccountOwner.USER.asString());

        return query(AccountsProvider.uriAccounts(), query);
    }

    private Account insertAccount(Currency currency) {
        final Account account = new Account();
        account.setTitle("a");
        account.setCurrency(currency);
        insert(AccountsProvider.uriAccounts(), account);
        return account;
    }
}
