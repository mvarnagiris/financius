package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.preferences.GeneralPrefs;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                CurrenciesProvider.class,
                CategoriesProvider.class,
                TagsProvider.class,
                AccountsProvider.class,
                TransactionsProvider.class,
                StartupService.class
        }
)
public class PersistenceModule {
    @Provides @Singleton public DBHelper provideDBHelper(@ApplicationContext Context context, CurrenciesManager currenciesManager) {
        return new DBHelper(context, currenciesManager);
    }

    @Provides @Singleton public CurrenciesManager provideCurrenciesManager(GeneralPrefs generalPrefs) {
        return new CurrenciesManager(generalPrefs);
    }

    @Provides public AmountFormatter provideAmountFormatter(CurrenciesManager currenciesManager) {
        return new AmountFormatter(currenciesManager);
    }
}
