package com.code44.finance.modules;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.services.StartupService;
import com.code44.finance.utils.IOUtils;

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
    @Provides @Singleton public DBHelper provideDBHelper(@ApplicationContext Context context) {
        return new DBHelper(context);
    }

    @Provides @Singleton @Main public Currency provideMainCurrency(DBHelper dbHelper) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT.getName() + "=?", "1")
                .from(dbHelper.getWritableDatabase(), Tables.Currencies.TABLE_NAME)
                .execute();

        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);
        return currency;
    }
}
