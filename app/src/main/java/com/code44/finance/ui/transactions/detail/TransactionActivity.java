package com.code44.finance.ui.transactions.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.activities.ModelActivity;
import com.code44.finance.ui.transactions.edit.TransactionEditActivity;
import com.code44.finance.utils.AccountUtils;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.analytics.Screens;
import com.google.common.base.Strings;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import javax.inject.Inject;

public class TransactionActivity extends ModelActivity<Transaction> {
    @Inject AmountFormatter amountFormatter;

    private TextView amountTextView;
    private TextView amountToTextView;
    private TextView dateTextView;
    private TextView accountTextView;
    private ImageView colorImageView;
    private TextView categoryTextView;
    private View tagsContainerView;
    private TextView tagsTextView;
    private View noteContainerView;
    private TextView noteTextView;

    public static void start(Context context, String transactionId) {
        makeActivityStarter(context, TransactionActivity.class, transactionId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        // Get views
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        amountToTextView = (TextView) findViewById(R.id.amountToTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        accountTextView = (TextView) findViewById(R.id.accountTextView);
        colorImageView = (ImageView) findViewById(R.id.colorImageView);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        tagsContainerView = findViewById(R.id.tagsContainerView);
        tagsTextView = (TextView) findViewById(R.id.tagsTextView);
        noteContainerView = findViewById(R.id.noteContainerView);
        noteTextView = (TextView) findViewById(R.id.noteTextView);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(this, TransactionsProvider.uriTransaction(modelId));
    }

    @NonNull @Override protected Transaction getModelFrom(@NonNull Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelLoaded(@NonNull Transaction model) {
        amountTextView.setText(amountFormatter.format(model));
        dateTextView.setText(DateUtils.formatDateTime(this, new DateTime(model.getDate()), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));
        accountTextView.setText(AccountUtils.getTitle(model));
        colorImageView.setColorFilter(CategoryUtils.getColor(this, model));
        categoryTextView.setText(CategoryUtils.getTitle(this, model));

        if (model.getTags().size() > 0) {
            final int tagBackgroundColor = ThemeUtils.getColor(this, R.attr.backgroundColorSecondary);
            final float tagBackgroundRadius = getResources().getDimension(R.dimen.tag_radius);
            final SpannableStringBuilder tags = new SpannableStringBuilder();
            for (Tag tag : model.getTags()) {
                tags.append(tag.getTitle());
                tags.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), tags.length() - tag.getTitle()
                        .length(), tags.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tags.append(" ");
            }
            tagsContainerView.setVisibility(View.VISIBLE);
            tagsTextView.setText(tags);
        } else {
            tagsContainerView.setVisibility(View.GONE);
        }

        if (Strings.isNullOrEmpty(model.getNote())) {
            noteContainerView.setVisibility(View.GONE);
        } else {
            noteContainerView.setVisibility(View.VISIBLE);
            noteTextView.setText(model.getNote());
        }

        switch (model.getTransactionType()) {
            case Expense:
                amountToTextView.setVisibility(View.GONE);
                break;
            case Income:
                amountToTextView.setVisibility(View.GONE);
                break;
            case Transfer:
                if (isSameCurrency(model)) {
                    amountToTextView.setVisibility(View.GONE);
                } else {
                    amountToTextView.setVisibility(View.VISIBLE);
                    amountToTextView.setText(amountFormatter.format(model.getAccountTo()
                                                                            .getCurrencyCode(), (long) (model.getAmount() * model.getExchangeRate())));
                }
                break;
        }
    }

    @Override protected void startModelEdit(@NonNull String modelId) {
        TransactionEditActivity.start(this, modelId);
    }

    @Nullable @Override protected Uri getDeleteUri() {
        return TransactionsProvider.uriTransactions();
    }

    @Nullable @Override protected Pair<String, String[]> getDeleteSelection(@NonNull String modelId) {
        return Pair.create(Tables.Transactions.ID + "=?", new String[]{modelId});
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Transaction;
    }

    private boolean isSameCurrency(Transaction transaction) {
        final Account accountFrom = transaction.getAccountFrom();
        final Account accountTo = transaction.getAccountTo();
        return accountFrom == null || accountTo == null || accountFrom.getCurrencyCode().equals(accountTo.getCurrencyCode());
    }
}
