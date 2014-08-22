package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.BaseModel;
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
        currency.getEntity().setExchangeRate(1.2345);
        insertCurrency(currency);

        final Cursor cursor = queryCurrencyCursor(currency.getEntity().getCode());
        assertEquals(1, cursor.getCount());
        assertEquals(currency.getEntity().getExchangeRate(), Currency.from(cursor).getEntity().getExchangeRate(), 0.0001);
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
        assertEquals(BaseModel.ItemState.DELETED_UNDO, Account.from(cursor).getItemState());
        IOUtils.closeQuietly(cursor);
    }

    @Test(expected = IllegalArgumentException.class)
    public void delete_throwsIllegalArgumentException_whenTryingToDeleteDefaultCurrency() {
        delete("delete", CurrenciesProvider.uriCurrencies(), null);
    }

    private Cursor queryDefaultCurrencyCursor() {
        final Query query = Query.create()
                .projectionId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT + "=?", "1");

        return query(CurrenciesProvider.uriCurrencies(), query);
    }

    private Cursor queryCurrencyCursor(String code) {
        final Query query = Query.create()
                .projectionId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?", code);

        return query(CurrenciesProvider.uriCurrencies(), query);
    }

    private Currency insertCurrency(boolean isDefault) {
        final Currency currency = new Currency();
        currency.getEntity().setCode("AAA");
        currency.getEntity().setDefault(isDefault);
        return insertCurrency(currency);
    }

    private Currency insertCurrency(Currency currency) {
        currency.setId(insert(CurrenciesProvider.uriCurrencies(), currency));
        return currency;
    }

    private int deleteCurrency(Currency currency) {
        return delete("delete", CurrenciesProvider.uriCurrencies(), Tables.Currencies.LOCAL_ID + "=?", String.valueOf(currency.getId()));
    }

    private Cursor queryAccountsCursor() {
        final Query query = Query.create()
                .projectionId(Tables.Accounts.ID)
                .projection(Tables.Accounts.PROJECTION)
                .selection(Tables.Accounts.OWNER + "=?", String.valueOf(Account.Owner.USER.asInt()));

        return query(AccountsProvider.uriAccounts(), query);
    }

    private Account insertAccount(Currency currency) {
        final Account account = new Account();
        account.setTitle("a");
        account.setCurrency(currency);
        account.setId(insert(AccountsProvider.uriAccounts(), account));
        return account;
    }
}
