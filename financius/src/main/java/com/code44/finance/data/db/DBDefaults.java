package com.code44.finance.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.db.model.Currency;

import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
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
                currency.setCode(code);
                currency.setSymbol(javaCurrency.getSymbol());
                currency.setDecimalCount(javaCurrency.getDefaultFractionDigits());
                currency.setDefault(code.equals(mainCurrencyCode));
                currency.checkValues();
                db.insert(Tables.Currencies.TABLE_NAME, null, currency.asContentValues());
            }
        }

        Currency.updateDefaultCurrency(db);
    }

    private static void addAccounts(SQLiteDatabase db) {
        final Account systemAccount = new Account();
        systemAccount.setServerId(UUID.randomUUID().toString());
        systemAccount.setOwner(Account.Owner.SYSTEM);
        systemAccount.checkValues();

        db.insert(Tables.Accounts.TABLE_NAME, null, systemAccount.asContentValues());
    }

    private static void addCategories(Context context, SQLiteDatabase db) {
        final Category expenseCategory = new Category();
        expenseCategory.setId(Category.EXPENSE_ID);
        expenseCategory.setTitle(context.getString(R.string.expense));
        expenseCategory.setColor(context.getResources().getColor(R.color.text_negative));
        expenseCategory.setType(Category.Type.EXPENSE);
        expenseCategory.setOwner(Category.Owner.SYSTEM);
        expenseCategory.setSortOrder(0);

        final Category incomeCategory = new Category();
        incomeCategory.setId(Category.INCOME_ID);
        incomeCategory.setTitle(context.getString(R.string.income));
        incomeCategory.setColor(context.getResources().getColor(R.color.text_positive));
        incomeCategory.setType(Category.Type.INCOME);
        incomeCategory.setOwner(Category.Owner.SYSTEM);
        incomeCategory.setSortOrder(0);

        final Category transferCategory = new Category();
        transferCategory.setId(Category.TRANSFER_ID);
        transferCategory.setTitle(context.getString(R.string.transfer));
        transferCategory.setColor(context.getResources().getColor(R.color.text_neutral));
        transferCategory.setType(Category.Type.TRANSFER);
        transferCategory.setOwner(Category.Owner.SYSTEM);
        transferCategory.setSortOrder(0);

        db.insert(Tables.Categories.TABLE_NAME, null, expenseCategory.asContentValues());
        db.insert(Tables.Categories.TABLE_NAME, null, incomeCategory.asContentValues());
        db.insert(Tables.Categories.TABLE_NAME, null, transferCategory.asContentValues());

        insertCategories(db, context.getResources().getStringArray(R.array.expense_categories), Category.Type.EXPENSE);
        insertCategories(db, context.getResources().getStringArray(R.array.income_categories), Category.Type.INCOME);
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

    private static void insertCategories(SQLiteDatabase db, String[] titles, Category.Type type) {
        int order = 0;
        final int startColor = new Random().nextInt(360);
        for (String title : titles) {
            final Category category = new Category();
            category.setTitle(title);
            category.setColor(Color.HSVToColor(new float[]{(startColor + (order * 200)) % 360, 0.7f, 0.7f}));
            category.setType(type);
            category.setOwner(Category.Owner.USER);
            category.setSortOrder(order++);
            db.insert(Tables.Categories.TABLE_NAME, null, category.asContentValues());
        }
    }
}
