package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.ui.common.ModelActivity;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.analytics.Analytics;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class TransactionActivity extends ModelActivity<Transaction> {
    private View containerView;
    private TextView dateTextView;
    private TextView amountTextView;
    private TextView amountToTextView;
    private TextView categoryTextView;
    private TextView noteTextView;
    private TextView tagsTextView;
    private TextView accountTextView;

    public static void start(Context context, String transactionId) {
        final Intent intent = makeIntent(context, TransactionActivity.class, transactionId);
        startActivity(context, intent);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_transaction;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        containerView = findViewById(R.id.containerView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        amountToTextView = (TextView) findViewById(R.id.amountToTextView);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        noteTextView = (TextView) findViewById(R.id.noteTextView);
        tagsTextView = (TextView) findViewById(R.id.tagsTextView);
        accountTextView = (TextView) findViewById(R.id.accountTextView);
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransaction(modelId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelLoaded(Transaction transaction) {
        final Category category = transaction.getCategory();
        final DateTime date = new DateTime(transaction.getDate());
        amountTextView.setText(MoneyFormatter.format(transaction));
        dateTextView.setText(DateUtils.formatDateTime(this, date, DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

        if (StringUtils.isEmpty(transaction.getNote())) {
            noteTextView.setVisibility(View.GONE);
        } else {
            noteTextView.setVisibility(View.VISIBLE);
            noteTextView.setText(transaction.getNote());
        }

        final int color = category != null ? category.getColor() : getResources().getColor(R.color.primary);
        containerView.setBackgroundColor(color);
        getToolbar().setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
            getWindow().setNavigationBarColor(color);
        }

        String categoryTitle = category != null ? category.getTitle() : null;
        switch (transaction.getTransactionType()) {
            case Expense:
                amountToTextView.setVisibility(View.GONE);
                if (StringUtils.isEmpty(categoryTitle)) {
                    categoryTitle = getString(R.string.expense);
                }
                break;
            case Income:
                amountToTextView.setVisibility(View.GONE);
                if (StringUtils.isEmpty(categoryTitle)) {
                    categoryTitle = getString(R.string.income);
                }
                break;
            case Transfer:
                if (isSameCurrency(transaction)) {
                    amountToTextView.setVisibility(View.GONE);
                } else {
                    amountToTextView.setVisibility(View.VISIBLE);
                    amountToTextView.setText(MoneyFormatter.format(transaction.getAccountTo().getCurrency(), (long) (transaction.getAmount() * transaction.getExchangeRate())));
                }

                if (StringUtils.isEmpty(categoryTitle)) {
                    categoryTitle = getString(R.string.transfer);
                }
                break;
        }

        categoryTextView.setTextColor(color);
        categoryTextView.setText(categoryTitle);

        if (transaction.getTags().size() > 0) {
            final int tagBackgroundColor = getResources().getColor(R.color.bg_secondary);
            final float tagBackgroundRadius = getResources().getDimension(R.dimen.tag_radius);
            final SpannableStringBuilder tags = new SpannableStringBuilder();
            for (Tag tag : transaction.getTags()) {
                tags.append(tag.getTitle());
                tags.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), tags.length() - tag.getTitle().length(), tags.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tags.append(" ");
            }
            tagsTextView.setVisibility(View.VISIBLE);
            tagsTextView.setText(tags);
        } else {
            tagsTextView.setVisibility(View.GONE);
        }

        final String unknownValue = "?";
        final String transferSymbol = " → ";
        final String account;
        switch (transaction.getTransactionType()) {
            case Expense:
                account = transaction.getAccountFrom() != null ? transaction.getAccountFrom().getTitle() : unknownValue;
                break;
            case Income:
                account = transaction.getAccountTo() != null ? transaction.getAccountTo().getTitle() : unknownValue;
                break;
            case Transfer:
                account = (transaction.getAccountFrom() != null ? transaction.getAccountFrom().getTitle() : unknownValue) + transferSymbol + (transaction.getAccountTo() != null ? transaction.getAccountTo().getTitle() : unknownValue);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
        }
        accountTextView.setText(account);
    }

    @Override protected Uri getDeleteUri() {
        return TransactionsProvider.uriTransactions();
    }

    @Override protected Pair<String, String[]> getDeleteSelection() {
        return Pair.create(Tables.Transactions.ID + "=?", new String[]{modelId});
    }

    @Override protected void startModelEdit(String modelId) {
        TransactionEditActivity.start(this, modelId);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.Transaction;
    }

    private boolean isSameCurrency(Transaction transaction) {
        final Account accountFrom = transaction.getAccountFrom();
        final Account accountTo = transaction.getAccountTo();
        return accountFrom == null || accountTo == null || accountFrom.getCurrency().equals(accountTo.getCurrency());
    }
}
