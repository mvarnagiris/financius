package com.code44.finance.data.providers;

import android.content.ContentResolver;

import org.junit.Before;
import org.robolectric.Robolectric;
import org.robolectric.shadows.ShadowContentResolver;

public class BaseContentProviderTestCase {
    protected ContentResolver contentResolver;

    @Before
    public void setUp() throws Exception {
        final CurrenciesProvider currenciesProvider = new CurrenciesProvider();
        currenciesProvider.onCreate();

        final AccountsProvider accountsProvider = new AccountsProvider();
        accountsProvider.onCreate();

        final CategoriesProvider categoriesProvider = new CategoriesProvider();
        categoriesProvider.onCreate();

        final TransactionsProvider transactionsProvider = new TransactionsProvider();
        transactionsProvider.onCreate();

        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(CurrenciesProvider.class), currenciesProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(AccountsProvider.class), accountsProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(CategoriesProvider.class), categoriesProvider);
        ShadowContentResolver.registerProvider(BaseProvider.getAuthority(TransactionsProvider.class), transactionsProvider);
        contentResolver = Robolectric.getShadowApplication().getContentResolver();
    }
}
