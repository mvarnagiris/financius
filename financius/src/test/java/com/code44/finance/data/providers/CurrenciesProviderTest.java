package com.code44.finance.data.providers;

import android.database.Cursor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CurrenciesProviderTest extends BaseContentProviderTestCase {
    @Test
    public void insert_makesSureThatThereIsOnlyOneDefaultCurrency() {
        Cursor c = contentResolver.query(CurrenciesProvider.uriCurrencies(), null, null, null, null);
        c.moveToFirst();
        int count = c.getCount();
        assertEquals(2, count);
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
