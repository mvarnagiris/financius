package com.code44.finance.data.providers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CurrenciesProviderTest {
    @Test
    public void insert_makesSureThatThereIsOnlyOneDefaultCurrency() {
        fail();
    }

    @Test
    public void update_doesNotAllowToChangeDefaultCurrency() {
        fail();
    }

    @Test
    public void bulkInsert_doesNotInsertDuplicateCurrencies() {
        fail();
    }

    @Test
    public void bulkInsert_allowsOnlyOneDefaultCurrency() {
        fail();
    }

    @Test
    public void bulkInsert_doesNotAllowToDeleteDefaultCurrency() {
        fail();
    }
}
