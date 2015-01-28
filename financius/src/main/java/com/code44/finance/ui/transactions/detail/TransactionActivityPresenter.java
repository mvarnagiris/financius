package com.code44.finance.ui.transactions.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.ui.transactions.TransactionEditActivity;
import com.code44.finance.utils.AccountUtils;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.ThemeUtils;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

class TransactionActivityPresenter extends ModelActivityPresenter<Transaction> implements LoaderManager.LoaderCallbacks<Cursor> {
    private final AmountFormatter amountFormatter;

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

    protected TransactionActivityPresenter(EventBus eventBus, AmountFormatter amountFormatter) {
        super(eventBus);
        this.amountFormatter = amountFormatter;
    }

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);

        amountTextView = findView(activity, R.id.amountTextView);
        amountToTextView = findView(activity, R.id.amountToTextView);
        dateTextView = findView(activity, R.id.dateTextView);
        accountTextView = findView(activity, R.id.accountTextView);
        colorImageView = findView(activity, R.id.colorImageView);
        categoryTextView = findView(activity, R.id.categoryTextView);
        tagsContainerView = findView(activity, R.id.tagsContainerView);
        tagsTextView = findView(activity, R.id.tagsTextView);
        noteContainerView = findView(activity, R.id.noteContainerView);
        noteTextView = findView(activity, R.id.noteTextView);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Transactions.getQuery().asCursorLoader(context, TransactionsProvider.uriTransaction(modelId));
    }

    @Override protected Transaction getModelFrom(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override protected void onModelLoaded(Transaction transaction) {
        amountTextView.setText(amountFormatter.format(transaction));
        dateTextView.setText(DateUtils.formatDateTime(getActivity(), new DateTime(transaction.getDate()), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));
        accountTextView.setText(AccountUtils.getTitle(transaction));
        colorImageView.setColorFilter(CategoryUtils.getColor(getActivity(), transaction));
        categoryTextView.setText(CategoryUtils.getTitle(getActivity(), transaction));

        if (transaction.getTags().size() > 0) {
            final int tagBackgroundColor = ThemeUtils.getColor(getActivity(), R.attr.backgroundColorSecondary);
            final float tagBackgroundRadius = getActivity().getResources().getDimension(R.dimen.tag_radius);
            final SpannableStringBuilder tags = new SpannableStringBuilder();
            for (Tag tag : transaction.getTags()) {
                tags.append(tag.getTitle());
                tags.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), tags.length() - tag.getTitle().length(), tags.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tags.append(" ");
            }
            tagsContainerView.setVisibility(View.VISIBLE);
            tagsTextView.setText(tags);
        } else {
            tagsContainerView.setVisibility(View.GONE);
        }

        if (Strings.isEmpty(transaction.getNote())) {
            noteContainerView.setVisibility(View.GONE);
        } else {
            noteContainerView.setVisibility(View.VISIBLE);
            noteTextView.setText(transaction.getNote());
        }

        switch (transaction.getTransactionType()) {
            case Expense:
                amountToTextView.setVisibility(View.GONE);
                break;
            case Income:
                amountToTextView.setVisibility(View.GONE);
                break;
            case Transfer:
                if (isSameCurrency(transaction)) {
                    amountToTextView.setVisibility(View.GONE);
                } else {
                    amountToTextView.setVisibility(View.VISIBLE);
                    amountToTextView.setText(amountFormatter.format(transaction.getAccountTo().getCurrencyCode(), (long) (transaction.getAmount() * transaction.getExchangeRate())));
                }
                break;
        }
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        TransactionEditActivity.start(context, modelId);
    }

    @Override protected Uri getDeleteUri() {
        return TransactionsProvider.uriTransactions();
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return Pair.create(Tables.Transactions.ID + "=?", new String[]{modelId});
    }

    private boolean isSameCurrency(Transaction transaction) {
        final Account accountFrom = transaction.getAccountFrom();
        final Account accountTo = transaction.getAccountTo();
        return accountFrom == null || accountTo == null || accountFrom.getCurrencyCode().equals(accountTo.getCurrencyCode());
    }
}
