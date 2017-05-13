package com.code44.finance.data.providers;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {CurrenciesProvider.class, CategoriesProvider.class, TagsProvider.class, AccountsProvider.class,
                   TransactionsProvider.class, ExchangeRatesProvider.class})
public class ProviderModule {
}
