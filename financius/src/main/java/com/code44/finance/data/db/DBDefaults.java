package com.code44.finance.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.text.TextUtils;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class DBDefaults {
    private final Context context;
    private final SQLiteDatabase database;

    public DBDefaults(Context context, SQLiteDatabase database) {
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(database, "Database cannot be null.");

        this.context = context;
        this.database = database;
    }

    public void addDefaults() {
        addCurrencies();
        addCategories();
    }

    private void addCurrencies() {
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
                currency.setId(UUID.randomUUID().toString());
                currency.setCode(code);
                currency.setSymbol(javaCurrency.getSymbol());
                currency.setDecimalCount(javaCurrency.getDefaultFractionDigits());
                currency.setDefault(code.equals(mainCurrencyCode));
                database.insert(Tables.Currencies.TABLE_NAME, null, currency.asValues());
            }
        }
    }

    private void addCategories() {
        insertCategories(context.getResources().getStringArray(R.array.expense_categories), context.getResources().getStringArray(R.array.expense_categories_colors), TransactionType.Expense);
        insertCategories(context.getResources().getStringArray(R.array.income_categories), context.getResources().getStringArray(R.array.income_categories_colors), TransactionType.Income);
    }

    private String getMainCurrencyCode() {
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

    private java.util.Currency getCurrencyFromCode(String code) {
        try {
            return java.util.Currency.getInstance(code);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void insertCategories(String[] titles, String[] colors, TransactionType type) {
        int order = 0;
        for (String title : titles) {
            final Category category = new Category();
            category.setId(UUID.randomUUID().toString());
            category.setTransactionType(type);
            category.setTitle(title);
            category.setColor(Color.parseColor(colors[order % colors.length]));
            category.setSortOrder(order++);
            database.insert(Tables.Categories.TABLE_NAME, null, category.asValues());
        }
    }
}
