package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.CurrencyFormat;
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
        assertEquals(CurrencyFormat.getDefault().getId(), CurrencyFormat.from(cursor).getId());
        IOUtils.closeQuietly(cursor);

        final CurrencyFormat currencyFormat = insertCurrency(true);
        cursor = queryDefaultCurrencyCursor();
        assertEquals(1, cursor.getCount());
        assertEquals(currencyFormat.getId(), CurrencyFormat.from(cursor).getId());
        assertEquals(currencyFormat.getId(), CurrencyFormat.getDefault().getId());
        IOUtils.closeQuietly(cursor);
    }

    @Test
    public void insert_updatesCurrency_whenCurrencyWithSameCodeExists() {
        final CurrencyFormat currencyFormat = insertCurrency(false);
        currencyFormat.setExchangeRate(1.2345);
        insertCurrency(currencyFormat);

        final Cursor cursor = queryCurrencyCursor(currencyFormat.getCode());
        assertEquals(1, cursor.getCount());
        assertEquals(currencyFormat.getExchangeRate(), CurrencyFormat.from(cursor).getExchangeRate(), 0.0001);
        IOUtils.closeQuietly(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_throwsIllegalArgumentException() {
        update(CurrenciesProvider.uriCurrencies(), new ContentValues(), null);
    }

    @Test
    public void deleteDelete_setsItemStateDeletedUndoForAccounts() {
        final CurrencyFormat currencyFormat = insertCurrency(false);
        insertAccount(currencyFormat);

        deleteCurrency(currencyFormat);
        final Cursor cursor = queryAccountsCursor();

        assertEquals(1, cursor.getCount());
        assertEquals(ModelState.DeletedUndo, Account.from(cursor).getModelState());
        IOUtils.closeQuietly(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_throwsIllegalArgumentException_whenTryingToDeleteDefaultCurrency() {
        delete("delete", CurrenciesProvider.uriCurrencies(), null);
    }

    private Cursor queryDefaultCurrencyCursor() {
        final Query query = Query.create()
                .projectionLocalId(Tables.CurrencyFormats.LOCAL_ID)
                .projection(Tables.CurrencyFormats.PROJECTION)
                .selection(Tables.CurrencyFormats.IS_DEFAULT + "=?", "1");

        return query(CurrenciesProvider.uriCurrencies(), query);
    }

    private Cursor queryCurrencyCursor(String code) {
        final Query query = Query.create()
                .projectionLocalId(Tables.CurrencyFormats.LOCAL_ID)
                .projection(Tables.CurrencyFormats.PROJECTION)
                .selection(Tables.CurrencyFormats.CODE + "=?", code);

        return query(CurrenciesProvider.uriCurrencies(), query);
    }

    private CurrencyFormat insertCurrency(boolean isDefault) {
        final CurrencyFormat currencyFormat = new CurrencyFormat();
        currencyFormat.setCode("AAA");
        currencyFormat.setDefault(isDefault);
        return insertCurrency(currencyFormat);
    }

    private CurrencyFormat insertCurrency(CurrencyFormat currencyFormat) {
        insert(CurrenciesProvider.uriCurrencies(), currencyFormat);
        return currencyFormat;
    }

    private int deleteCurrency(CurrencyFormat currencyFormat) {
        return delete("delete", CurrenciesProvider.uriCurrencies(), Tables.CurrencyFormats.LOCAL_ID + "=?", String.valueOf(currencyFormat.getId()));
    }

    private Cursor queryAccountsCursor() {
        final Query query = Query.create()
                .projectionLocalId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .selection(Tables.Accounts.OWNER + "=?", AccountOwner.USER.asString());

        return query(AccountsProvider.uriAccounts(), query);
    }

    private Account insertAccount(CurrencyFormat currencyFormat) {
        final Account account = new Account();
        account.setTitle("a");
        account.setCurrencyCode(currencyFormat);
        insert(AccountsProvider.uriAccounts(), account);
        return account;
    }
}
