package com.code44.finance.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;

import com.code44.finance.R;
import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.common.model.CategoryOwner;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class DBDefaults {
    private DBDefaults() {
    }

    public static void addDefaults(Context context, SQLiteDatabase db) {
        addCurrencies(db);
        addAccounts(db);
        addCategories(context, db);
    }

    private static void addCurrencies(SQLiteDatabase db) {
        final Set<String> currencyCodes = new HashSet<>();
        final String mainCurrencyCode = getMainCurrencyCode();
        currencyCodes.add(mainCurrencyCode);

        // Popular currencies
        currencyCodes.add("USD");
        currencyCodes.add("EUR");
        currencyCodes.add("GBP");
        currencyCodes.add("CNY");
        currencyCodes.add("INR");
        currencyCodes.add("RUB");
        currencyCodes.add("JPY");

        // Create currencies
        for (String code : currencyCodes) {
            java.util.Currency javaCurrency = getCurrencyFromCode(code);
            if (javaCurrency != null) {
                Currency currency = new Currency();
                currency.setServerId(UUID.randomUUID().toString());
                currency.setCode(code);
                currency.setSymbol(javaCurrency.getSymbol());
                currency.setDecimalCount(javaCurrency.getDefaultFractionDigits());
                currency.setDefault(code.equals(mainCurrencyCode));
                db.insert(Tables.Currencies.TABLE_NAME, null, currency.asContentValues());
            }
        }

        Currency.updateDefaultCurrency(db);
    }

    private static void addAccounts(SQLiteDatabase db) {
        final Account systemAccount = new Account();
        systemAccount.setServerId(UUID.randomUUID().toString());
        systemAccount.setAccountOwner(AccountOwner.SYSTEM);

        db.insert(Tables.Accounts.TABLE_NAME, null, systemAccount.asContentValues());
    }

    private static void addCategories(Context context, SQLiteDatabase db) {
        final Category expenseCategory = new Category();
        expenseCategory.setId(Category.EXPENSE_ID);
        expenseCategory.setServerId(UUID.randomUUID().toString());
        expenseCategory.setTitle(context.getString(R.string.expense));
        expenseCategory.setColor(context.getResources().getColor(R.color.text_negative));
        expenseCategory.setCategoryType(CategoryType.EXPENSE);
        expenseCategory.setCategoryOwner(CategoryOwner.SYSTEM);
        expenseCategory.setSortOrder(0);

        final Category incomeCategory = new Category();
        incomeCategory.setId(Category.INCOME_ID);
        incomeCategory.setServerId(UUID.randomUUID().toString());
        incomeCategory.setTitle(context.getString(R.string.income));
        incomeCategory.setColor(context.getResources().getColor(R.color.text_positive));
        incomeCategory.setCategoryType(CategoryType.INCOME);
        incomeCategory.setCategoryOwner(CategoryOwner.SYSTEM);
        incomeCategory.setSortOrder(0);

        final Category transferCategory = new Category();
        transferCategory.setId(Category.TRANSFER_ID);
        transferCategory.setServerId(UUID.randomUUID().toString());
        transferCategory.setTitle(context.getString(R.string.transfer));
        transferCategory.setColor(context.getResources().getColor(R.color.text_neutral));
        transferCategory.setCategoryType(CategoryType.TRANSFER);
        transferCategory.setCategoryOwner(CategoryOwner.SYSTEM);
        transferCategory.setSortOrder(0);

        db.insert(Tables.Categories.TABLE_NAME, null, expenseCategory.asContentValues());
        db.insert(Tables.Categories.TABLE_NAME, null, incomeCategory.asContentValues());
        db.insert(Tables.Categories.TABLE_NAME, null, transferCategory.asContentValues());

        insertCategories(db, context.getResources().getStringArray(R.array.expense_categories), context.getResources().getStringArray(R.array.expense_categories_colors), CategoryType.EXPENSE);
        insertCategories(db, context.getResources().getStringArray(R.array.income_categories), context.getResources().getStringArray(R.array.income_categories_colors), CategoryType.INCOME);
    }

    private static String getMainCurrencyCode() {
        String code = null;
        try {
            code = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        } catch (Exception ignored) {
        }

        if (TextUtils.isEmpty(code)) {
            code = "USD";
        }

        return code;
    }

    private static java.util.Currency getCurrencyFromCode(String code) {
        try {
            return java.util.Currency.getInstance(code);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void insertCategories(SQLiteDatabase db, String[] titles, String[] colors, CategoryType type) {
        int order = 0;
        for (String title : titles) {
            final Category category = new Category();
            category.setServerId(UUID.randomUUID().toString());
            category.setTitle(title);
            category.setColor(Color.parseColor(colors[order % colors.length]));
            category.setCategoryType(type);
            category.setCategoryOwner(CategoryOwner.USER);
            category.setSortOrder(order++);
            db.insert(Tables.Categories.TABLE_NAME, null, category.asContentValues());
        }
    }
}
