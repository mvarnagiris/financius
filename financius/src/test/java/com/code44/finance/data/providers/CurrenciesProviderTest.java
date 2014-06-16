package com.code44.finance.data.providers;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Currency;

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

        assertQuerySize(CurrenciesProvider.uriCurrencies(), Query.get().projectionId(Tables.Currencies.ID).selection(Tables.Currencies.IS_DEFAULT + "=?", "1"), 1);
        // TODO Assert that default currency is AAA
    }

    @Test
    public void insert_updatesCurrency_whenCurrencyWithSameCodeExists() {
        fail();
    }

    @Test
    public void update_doesNotAllowToChangeDefaultCurrency() {
        fail();
    }

    @Test
    public void delete_setsTheSameItemStateForAccounts() {
        fail();
    }

    @Test
    public void delete_doesNotAllowToDeleteDefaultCurrency() {
        fail();
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
