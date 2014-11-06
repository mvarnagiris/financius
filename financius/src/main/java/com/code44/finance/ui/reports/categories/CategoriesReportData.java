package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CategoriesReportData {
    private final PieChartData pieChartData;
    private final List<CategoriesReportItem> categoriesReportItems;

    public CategoriesReportData(Context context, Cursor cursor, Currency mainCurrency, TransactionType transactionType) {
        final Map<Category, Long> categoryExpenses = new HashMap<>();
        final Map<Category, Map<Tag, Long>> categoryTagExpenses = new HashMap<>();

        if (cursor.moveToFirst()) {
            final Category noCategory = createNoCategory(context, transactionType);
            do {
                final Transaction transaction = Transaction.from(cursor);
                if (isTransactionValid(transaction, transactionType)) {
                    final Long amount = getAmount(transaction, mainCurrency);
                    final Category category = transaction.getCategory() == null ? noCategory : transaction.getCategory();
                    increaseCategoryExpenseAmount(categoryExpenses, category, amount);
                    increaseCategoryTagsExpenses(categoryTagExpenses, transaction, category, amount);
                }
            } while (cursor.moveToNext());
        }

        final TreeMap<Category, Long> sortedExpenses = new TreeMap<>(new CategoryExpenseComparator(categoryExpenses));
        sortedExpenses.putAll(categoryExpenses);

        categoriesReportItems = new ArrayList<>();
        final PieChartData.Builder builder = PieChartData.builder();
        for (Category category : sortedExpenses.descendingKeySet()) {
            final Long amount = sortedExpenses.get(category);
            builder.addValues(new PieChartValue(amount, category.getColor()));

            final List<Pair<Tag, Long>> tags;
            final Map<Tag, Long> tagExpenses = categoryTagExpenses.get(category);
            if (tagExpenses != null) {
                final TreeMap<Tag, Long> sortedTagExpenses = new TreeMap<>(new TagExpenseComparator(tagExpenses));
                sortedTagExpenses.putAll(tagExpenses);

                tags = new ArrayList<>();
                for (Tag tag : sortedTagExpenses.descendingKeySet()) {
                    tags.add(Pair.create(tag, sortedTagExpenses.get(tag)));
                }
            } else {
                tags = Collections.emptyList();
            }

            categoriesReportItems.add(new CategoriesReportItem(category, amount, tags));
        }
        pieChartData = builder.build();
    }

    public PieChartData getPieChartData() {
        return pieChartData;
    }

    public int size() {
        return categoriesReportItems.size();
    }

    public CategoriesReportItem get(int position) {
        return categoriesReportItems.get(position);
    }

    private Category createNoCategory(Context context, TransactionType transactionType) {
        final Category noCategory = new Category();
        noCategory.setId("0");
        noCategory.setTitle(context.getString(R.string.no_category));
        noCategory.setColor(context.getResources().getColor(transactionType == TransactionType.Expense ? R.color.text_negative : R.color.text_positive));
        return noCategory;
    }

    private boolean isTransactionValid(Transaction transaction, TransactionType transactionType) {
        return transaction.includeInReports() && transaction.getTransactionType() == transactionType && transaction.getTransactionState() == TransactionState.Confirmed;
    }

    private Long getAmount(Transaction transaction, Currency mainCurrency) {
        final Currency currency = transaction.getTransactionType() == TransactionType.Expense ? transaction.getAccountFrom().getCurrency() : transaction.getAccountTo().getCurrency();

        if (currency.getId().equals(mainCurrency.getId())) {
            return transaction.getAmount();
        } else {
            return Math.round(transaction.getAmount() * currency.getExchangeRate());
        }
    }

    private void increaseCategoryExpenseAmount(Map<Category, Long> categoryExpenses, Category category, Long amount) {
        Long totalExpenseForCategory = categoryExpenses.get(category);
        if (totalExpenseForCategory == null) {
            totalExpenseForCategory = amount;
        } else {
            totalExpenseForCategory += amount;
        }
        categoryExpenses.put(category, totalExpenseForCategory);
    }

    private void increaseCategoryTagsExpenses(Map<Category, Map<Tag, Long>> categoryTagExpenses, Transaction transaction, Category category, Long amount) {
        final List<Tag> tags = transaction.getTags();
        for (Tag tag : tags) {
            Map<Tag, Long> tagExpense = categoryTagExpenses.get(category);
            Long tagExpenseAmount;
            if (tagExpense == null) {
                tagExpense = new HashMap<>();
                tagExpenseAmount = amount;
            } else {
                tagExpenseAmount = tagExpense.get(tag) + amount;
            }
            tagExpense.put(tag, tagExpenseAmount);
            categoryTagExpenses.put(category, tagExpense);
        }
    }

    public static class CategoriesReportItem {
        private final Category category;
        private final long amount;
        private final List<Pair<Tag, Long>> tags;

        public CategoriesReportItem(Category category, long amount, List<Pair<Tag, Long>> tags) {
            this.category = category;
            this.amount = amount;
            this.tags = tags;
        }

        public Category getCategory() {
            return category;
        }

        public long getAmount() {
            return amount;
        }

        public List<Pair<Tag, Long>> getTags() {
            return tags;
        }
    }
}
