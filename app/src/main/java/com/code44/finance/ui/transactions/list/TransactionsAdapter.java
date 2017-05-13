package com.code44.finance.ui.transactions.list;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.interval.IntervalType;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelViewHolder;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.recycler.SectionsDecoration;
import com.code44.finance.utils.AccountUtils;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.interval.BaseInterval;
import com.google.common.base.Strings;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import static com.google.common.base.Preconditions.checkNotNull;

class TransactionsAdapter extends ModelsAdapter<Transaction, TransactionsAdapter.ViewHolder> implements SectionsDecoration.Adapter<TransactionsAdapter.HeaderViewHolder> {
    private static final String UNKNOWN_VALUE = "?";
    private static final String TRANSFER_SYMBOL = " → ";

    private final AmountFormatter amountFormatter;
    private final BaseInterval interval;
    private final int textColorPrimary;
    private final int tagBackgroundColor;
    private final float tagBackgroundRadius;
    private final int expenseAmountColor;
    private final int incomeAmountColor;
    private final int transferAmountColor;
    private final int weakColor;

    public TransactionsAdapter(@NonNull OnModelClickListener<Transaction> onModelClickListener, @NonNull ModelsActivity.Mode mode, @NonNull Context context, @NonNull AmountFormatter amountFormatter, @NonNull BaseInterval interval) {
        super(onModelClickListener, mode);
        checkNotNull(context, "Context cannot be null.");
        this.amountFormatter = checkNotNull(amountFormatter, "AmountFormatter cannot be null.");
        this.interval = checkNotNull(interval, "Interval cannot be null.");

        textColorPrimary = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        tagBackgroundColor = ThemeUtils.getColor(context, R.attr.backgroundColorSecondary);
        tagBackgroundRadius = context.getResources().getDimension(R.dimen.tag_radius);
        expenseAmountColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        incomeAmountColor = ThemeUtils.getColor(context, R.attr.textColorPositive);
        transferAmountColor = ThemeUtils.getColor(context, R.attr.textColorNeutral);
        weakColor = ThemeUtils.getColor(context, android.R.attr.textColorSecondary);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_transaction, parent, false), this);
    }

    @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Transaction model, boolean isSelected) {
        final DateTime date = new DateTime(model.getDate());

        holder.weekdayTextView.setText(date.dayOfWeek().getAsShortText());
        holder.dayTextView.setText(date.dayOfMonth().getAsShortText());
        holder.colorImageView.setColorFilter(CategoryUtils.getColor(holder.itemView.getContext(), model));
        holder.amountTextView.setText(amountFormatter.format(model));
        holder.titleTextView.setTextColor(textColorPrimary);
        holder.titleTextView.setText(getTitle(holder.titleTextView.getContext(), model));
        holder.subtitleTextView.setText(getSubtitle(model));
        bindViewForTransactionType(holder, model);

        if (model.getTransactionState() == TransactionState.Pending) {
            bindViewPending(holder, model);
        }
    }

    @Override protected Transaction modelFromCursor(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override public long getHeaderId(int position) {
        getCursor().moveToPosition(position);
        final TransactionState transactionState = TransactionState.fromInt(getCursor().getInt(getCursor().getColumnIndex(Tables.Transactions.STATE
                                                                                                                                 .getName())));
        if (transactionState == TransactionState.Pending) {
            return 0;
        }

        final IntervalType intervalType = interval.getIntervalType();
        final long date = getCursor().getLong(getCursor().getColumnIndex(Tables.Transactions.DATE.getName()));
        final Period period = BaseInterval.getPeriod(intervalType, interval.getLength());
        final Interval interval = BaseInterval.getInterval(date, period, intervalType);
        return interval.getStartMillis();
    }

    @Override public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_transaction_header, parent, false));
    }

    @Override public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);

        final String title;
        final TransactionState transactionState = TransactionState.fromInt(cursor.getInt(cursor.getColumnIndex(Tables.Transactions.STATE.getName())));
        if (transactionState == TransactionState.Confirmed) {
            final IntervalType intervalType = interval.getIntervalType();
            final long date = cursor.getLong(cursor.getColumnIndex(Tables.Transactions.DATE.getName()));
            final Period period = BaseInterval.getPeriod(intervalType, interval.getLength());
            final Interval interval = BaseInterval.getInterval(date, period, intervalType);
            title = BaseInterval.getTitle(viewHolder.itemView.getContext(), interval, intervalType);
        } else {
            title = viewHolder.itemView.getContext().getString(R.string.pending);
        }
        viewHolder.titleTextView.setText(title);
    }

    private void bindViewForTransactionType(ViewHolder holder, Transaction transaction) {
        final String account = AccountUtils.getTitle(transaction);
        final int amountColor;
        switch (transaction.getTransactionType()) {
            case Expense:
                amountColor = expenseAmountColor;
                break;
            case Income:
                amountColor = incomeAmountColor;
                break;
            case Transfer:
                amountColor = transferAmountColor;
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
        }

        holder.accountTextView.setText(account);
        holder.amountTextView.setTextColor(amountColor);
    }

    private void bindViewPending(ViewHolder holder, Transaction transaction) {
        final boolean isCategoryUnknown = transaction.getCategory() == null || !transaction.getCategory().hasId();
        final boolean isTransfer = transaction.getTransactionType() == TransactionType.Transfer;

        if (isCategoryUnknown && !isTransfer) {
            holder.titleTextView.setTextColor(weakColor);
            holder.colorImageView.setColorFilter(weakColor);
        }

        if (transaction.getAmount() == 0) {
            holder.amountTextView.setTextColor(weakColor);
        }

        final boolean isAccountFromUnknown = transaction.getAccountFrom() == null || !transaction.getAccountFrom().hasId();
        final boolean isAccountToUnknown = transaction.getAccountTo() == null || !transaction.getAccountTo().hasId();
        final boolean isExpense = transaction.getTransactionType() == TransactionType.Expense;
        final boolean isIncome = transaction.getTransactionType() == TransactionType.Income;
        if (isExpense) {
            if (isAccountFromUnknown) {
                holder.accountTextView.setText(UNKNOWN_VALUE);
            }

            if (transaction.getAmount() > 0) {
                holder.amountTextView.setTextColor(expenseAmountColor);
            }
        } else if (isIncome) {
            if (isAccountToUnknown) {
                holder.accountTextView.setText(UNKNOWN_VALUE);
            }

            if (transaction.getAmount() > 0) {
                holder.amountTextView.setTextColor(incomeAmountColor);
            }
        } else {
            final String accountFrom = isAccountFromUnknown ? UNKNOWN_VALUE : transaction.getAccountFrom().getTitle();
            final String accountTo = isAccountToUnknown ? UNKNOWN_VALUE : transaction.getAccountTo().getTitle();
            holder.accountTextView.setText(accountFrom + TRANSFER_SYMBOL + accountTo);
            if (transaction.getAmount() > 0) {
                holder.amountTextView.setTextColor(transferAmountColor);
            }
        }
    }

    private String getTitle(Context context, Transaction transaction) {
        if (Strings.isNullOrEmpty(transaction.getNote())) {
            return CategoryUtils.getTitle(context, transaction);
        } else {
            return transaction.getNote();
        }
    }

    private CharSequence getSubtitle(Transaction transaction) {
        if (transaction.getTags().size() > 0) {
            final SpannableStringBuilder subtitle = new SpannableStringBuilder();
            for (Tag tag : transaction.getTags()) {
                subtitle.append(tag.getTitle());
                subtitle.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), subtitle.length() - tag.getTitle()
                        .length(), subtitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                subtitle.append(" ");
            }
            return subtitle;
        } else if (transaction.getCategory() != null && !Strings.isNullOrEmpty(transaction.getNote())) {
            return transaction.getCategory().getTitle();
        }
        return null;
    }

    static class ViewHolder extends ModelViewHolder {
        private final ImageView colorImageView;
        private final TextView weekdayTextView;
        private final TextView dayTextView;
        private final TextView titleTextView;
        private final TextView subtitleTextView;
        private final TextView amountTextView;
        private final TextView accountTextView;

        public ViewHolder(@NonNull View itemView, @NonNull OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            colorImageView = (ImageView) itemView.findViewById(R.id.colorImageView);
            weekdayTextView = (TextView) itemView.findViewById(R.id.weekdayTextView);
            dayTextView = (TextView) itemView.findViewById(R.id.dayTextView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitleTextView);
            amountTextView = (TextView) itemView.findViewById(R.id.amountTextView);
            accountTextView = (TextView) itemView.findViewById(R.id.accountTextView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }
}
