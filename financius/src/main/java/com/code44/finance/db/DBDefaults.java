package com.code44.finance.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.db.model.Account;
import com.code44.finance.db.model.Category;
import com.code44.finance.db.model.Currency;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nl.qbusict.cupboard.CupboardFactory;

public final class DBDefaults {
    private DBDefaults() {
    }

    public static void addDefaults(SQLiteDatabase db) {
        addCurrencies(db);
        addAccounts(db);
        addCategories(db);
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
                currency.useDefaultsIfNotSet();
                currency.setCode(code);
                currency.setSymbol(javaCurrency.getSymbol());
                currency.setDecimalCount(javaCurrency.getDefaultFractionDigits());
                currency.setDefault(code.equals(mainCurrencyCode));
                currency.checkRequiredValues();
                CupboardFactory.cupboard().withDatabase(db).put(currency);
            }
        }
    }

    private static void addAccounts(SQLiteDatabase db) {
        final Account systemAccount = new Account();
        systemAccount.useDefaultsIfNotSet();
        systemAccount.setOwner(Account.Owner.SYSTEM);
        systemAccount.checkRequiredValues();

        CupboardFactory.cupboard().withDatabase(db).put(systemAccount);
    }

    private static void addCategories(SQLiteDatabase db) {
        final Category expenseCategory = new Category();
        expenseCategory.useDefaultsIfNotSet();
        expenseCategory.setId(Category.EXPENSE_ID);
        expenseCategory.setTitle(App.getAppContext().getString(R.string.expense));
        expenseCategory.setType(Category.Type.EXPENSE);
        expenseCategory.setOwner(Category.Owner.SYSTEM);

        final Category incomeCategory = new Category();
        incomeCategory.useDefaultsIfNotSet();
        incomeCategory.setId(Category.INCOME_ID);
        incomeCategory.setTitle(App.getAppContext().getString(R.string.income));
        incomeCategory.setType(Category.Type.INCOME);
        incomeCategory.setOwner(Category.Owner.SYSTEM);

        final Category transferCategory = new Category();
        transferCategory.useDefaultsIfNotSet();
        transferCategory.setId(Category.TRANSFER_ID);
        transferCategory.setTitle(App.getAppContext().getString(R.string.transfer));
        transferCategory.setType(Category.Type.TRANSFER);
        transferCategory.setOwner(Category.Owner.SYSTEM);

        CupboardFactory.cupboard().withDatabase(db).put(expenseCategory, incomeCategory, transferCategory);

        insertCategories(db, App.getAppContext().getResources().getStringArray(R.array.expense_categories), Category.Type.EXPENSE);
        insertCategories(db, App.getAppContext().getResources().getStringArray(R.array.income_categories), Category.Type.INCOME);
    }

    private static String getMainCurrencyCode() {
        String code = null;
        try {
            code = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        } catch (Exception ignored) {
        }

        if (TextUtils.isEmpty(code))
            code = "USD";

        return code;
    }

    private static java.util.Currency getCurrencyFromCode(String code) {
        try {
            return java.util.Currency.getInstance(code);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static void insertCategories(SQLiteDatabase db, String[] titles, Category.Type type) {
        final List<Category> categories = new ArrayList<>();
        int order = 0;
        for (String title : titles) {
            final Category category = new Category();
            category.useDefaultsIfNotSet();
            category.setTitle(title);
            category.setType(type);
            category.setOwner(Category.Owner.SYSTEM);
            category.setOrder(order++);
            categories.add(category);
        }
        CupboardFactory.cupboard().withDatabase(db).put(categories);
    }
}
