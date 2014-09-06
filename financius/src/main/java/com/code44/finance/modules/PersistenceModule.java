package com.code44.finance.modules;

import android.content.Context;
import android.database.Cursor;

import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.qualifiers.Expense;
import com.code44.finance.qualifiers.Income;
import com.code44.finance.qualifiers.Transfer;
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

    @Provides @Singleton public Currency provideDefaultCurrency(@ApplicationContext Context context) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Currencies.LOCAL_ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.IS_DEFAULT.getName() + "=?", "1")
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();

        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);
        return currency;
    }

    @Provides @Singleton public Account provideSystemAccount(@ApplicationContext Context context) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Accounts.LOCAL_ID)
                .projection(Tables.Accounts.PROJECTION)
                .selection(Tables.Accounts.OWNER.getName() + "=?", AccountOwner.SYSTEM.asString())
                .from(context, AccountsProvider.uriAccounts())
                .execute();

        final Account systemAccount = Account.from(cursor);
        IOUtils.closeQuietly(cursor);
        return systemAccount;
    }

    @Provides @Singleton @Expense public Category provideExpenseCategory(@ApplicationContext Context context) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Categories.LOCAL_ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.LOCAL_ID + "=?", String.valueOf(Category.EXPENSE_ID))
                .from(context, CategoriesProvider.uriCategories())
                .execute();

        final Category expenseCategory = Category.from(cursor);
        IOUtils.closeQuietly(cursor);
        return expenseCategory;
    }

    @Provides @Singleton @Income public Category provideIncomeCategory(@ApplicationContext Context context) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Categories.LOCAL_ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.LOCAL_ID + "=?", String.valueOf(Category.INCOME_ID))
                .from(context, CategoriesProvider.uriCategories())
                .execute();

        final Category incomeCategory = Category.from(cursor);
        IOUtils.closeQuietly(cursor);
        return incomeCategory;
    }

    @Provides @Singleton @Transfer public Category provideTransferCategory(@ApplicationContext Context context) {
        final Cursor cursor = Query.create()
                .projectionLocalId(Tables.Categories.LOCAL_ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.LOCAL_ID + "=?", String.valueOf(Category.TRANSFER_ID))
                .from(context, CategoriesProvider.uriCategories())
                .execute();

        final Category transferCategory = Category.from(cursor);
        IOUtils.closeQuietly(cursor);
        return transferCategory;
    }
}
