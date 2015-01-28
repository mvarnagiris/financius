package com.code44.finance.ui.transactions.list;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.common.recycler.SectionsDecoration;
import com.code44.finance.utils.AccountUtils;
import com.code44.finance.utils.CategoryUtils;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.interval.BaseInterval;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

class TransactionsAdapter extends ModelsAdapter<Transaction> implements SectionsDecoration.Adapter<TransactionsAdapter.HeaderViewHolder> {
    private static final String UNKNOWN_VALUE = "?";
    private static final String TRANSFER_SYMBOL = " → ";

    private final AmountFormatter amountFormatter;
    private final BaseInterval interval;

    public TransactionsAdapter(OnModelClickListener<Transaction> onModelClickListener, AmountFormatter amountFormatter, BaseInterval interval) {
        super(onModelClickListener);
        this.amountFormatter = amountFormatter;
        this.interval = interval;
    }

    @Override protected ModelViewHolder<Transaction> createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_transaction, parent, false), amountFormatter);
    }

    @Override protected Transaction modelFromCursor(Cursor cursor) {
        return Transaction.from(cursor);
    }

    @Override public long getHeaderId(int position) {
        getCursor().moveToPosition(position);
        final TransactionState transactionState = TransactionState.fromInt(getCursor().getInt(getCursor().getColumnIndex(Tables.Transactions.STATE.getName())));
        if (transactionState == TransactionState.Pending) {
            return 0;
        }

        final BaseInterval.Type type = interval.getType();
        final long date = getCursor().getLong(getCursor().getColumnIndex(Tables.Transactions.DATE.getName()));
        final Period period = BaseInterval.getPeriod(type, interval.getLength());
        final Interval interval = BaseInterval.getInterval(date, period, type);
        return interval.getStartMillis();
    }

    @Override public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_transaction_header, parent, false));
    }

    @Override public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        getCursor().moveToPosition(position);
        viewHolder.bind(getCursor(), interval);
    }

    private static class ViewHolder extends ModelViewHolder<Transaction> {
        private final AmountFormatter amountFormatter;
        private final int textColorPrimary;
        private final int tagBackgroundColor;
        private final float tagBackgroundRadius;
        private final int expenseAmountColor;
        private final int incomeAmountColor;
        private final int transferAmountColor;
        private final int weakColor;

        private final ImageView colorImageView;
        private final TextView weekdayTextView;
        private final TextView dayTextView;
        private final TextView titleTextView;
        private final TextView subtitleTextView;
        private final TextView amountTextView;
        private final TextView accountTextView;

        public ViewHolder(View itemView, AmountFormatter amountFormatter) {
            super(itemView);
            this.amountFormatter = amountFormatter;

            textColorPrimary = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            tagBackgroundColor = ThemeUtils.getColor(itemView.getContext(), R.attr.backgroundColorSecondary);
            tagBackgroundRadius = itemView.getContext().getResources().getDimension(R.dimen.tag_radius);
            expenseAmountColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            incomeAmountColor = ThemeUtils.getColor(itemView.getContext(), R.attr.textColorPositive);
            transferAmountColor = ThemeUtils.getColor(itemView.getContext(), R.attr.textColorNeutral);
            weakColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorSecondary);

            colorImageView = (ImageView) itemView.findViewById(R.id.colorImageView);
            weekdayTextView = (TextView) itemView.findViewById(R.id.weekdayTextView);
            dayTextView = (TextView) itemView.findViewById(R.id.dayTextView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitleTextView);
            amountTextView = (TextView) itemView.findViewById(R.id.amountTextView);
            accountTextView = (TextView) itemView.findViewById(R.id.accountTextView);
        }

        @Override protected void bind(Transaction transaction, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            final DateTime date = new DateTime(transaction.getDate());

            // Set values
            weekdayTextView.setText(date.dayOfWeek().getAsShortText());
            dayTextView.setText(date.dayOfMonth().getAsShortText());
            colorImageView.setColorFilter(CategoryUtils.getColor(itemView.getContext(), transaction));
            amountTextView.setText(amountFormatter.format(transaction));
            titleTextView.setTextColor(textColorPrimary);
            titleTextView.setText(getTitle(titleTextView.getContext(), transaction));
            subtitleTextView.setText(getSubtitle(transaction));
            bindViewForTransactionType(transaction);

            if (transaction.getTransactionState() == TransactionState.Pending) {
                bindViewPending(transaction);
            }
        }

        private void bindViewForTransactionType(Transaction transaction) {
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

            accountTextView.setText(account);
            amountTextView.setTextColor(amountColor);
        }

        private void bindViewPending(Transaction transaction) {
            final boolean isCategoryUnknown = transaction.getCategory() == null || !transaction.getCategory().hasId();
            final boolean isTransfer = transaction.getTransactionType() == TransactionType.Transfer;

            if (isCategoryUnknown && !isTransfer) {
                titleTextView.setTextColor(weakColor);
                colorImageView.setColorFilter(weakColor);
            }

            if (transaction.getAmount() == 0) {
                amountTextView.setTextColor(weakColor);
            }

            final boolean isAccountFromUnknown = transaction.getAccountFrom() == null || !transaction.getAccountFrom().hasId();
            final boolean isAccountToUnknown = transaction.getAccountTo() == null || !transaction.getAccountTo().hasId();
            final boolean isExpense = transaction.getTransactionType() == TransactionType.Expense;
            final boolean isIncome = transaction.getTransactionType() == TransactionType.Income;
            if (isExpense) {
                if (isAccountFromUnknown) {
                    accountTextView.setText(UNKNOWN_VALUE);
                }

                if (transaction.getAmount() > 0) {
                    amountTextView.setTextColor(expenseAmountColor);
                }
            } else if (isIncome) {
                if (isAccountToUnknown) {
                    accountTextView.setText(UNKNOWN_VALUE);
                }

                if (transaction.getAmount() > 0) {
                    amountTextView.setTextColor(incomeAmountColor);
                }
            } else {
                final String accountFrom = isAccountFromUnknown ? UNKNOWN_VALUE : transaction.getAccountFrom().getTitle();
                final String accountTo = isAccountToUnknown ? UNKNOWN_VALUE : transaction.getAccountTo().getTitle();
                accountTextView.setText(accountFrom + TRANSFER_SYMBOL + accountTo);
                if (transaction.getAmount() > 0) {
                    amountTextView.setTextColor(transferAmountColor);
                }
            }
        }

        private String getTitle(Context context, Transaction transaction) {
            if (Strings.isEmpty(transaction.getNote())) {
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
                    subtitle.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), subtitle.length() - tag.getTitle().length(), subtitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    subtitle.append(" ");
                }
                return subtitle;
            } else if (transaction.getCategory() != null && !Strings.isEmpty(transaction.getNote())) {
                return transaction.getCategory().getTitle();
            }
            return null;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView title_TV;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title_TV = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        public void bind(Cursor cursor, BaseInterval baseInterval) {
            final String title;
            final TransactionState transactionState = TransactionState.fromInt(cursor.getInt(cursor.getColumnIndex(Tables.Transactions.STATE.getName())));
            if (transactionState == TransactionState.Confirmed) {
                final BaseInterval.Type type = baseInterval.getType();
                final long date = cursor.getLong(cursor.getColumnIndex(Tables.Transactions.DATE.getName()));
                final Period period = BaseInterval.getPeriod(type, baseInterval.getLength());
                final Interval interval = BaseInterval.getInterval(date, period, type);
                title = BaseInterval.getTitle(itemView.getContext(), interval, type);
            } else {
                title = itemView.getContext().getString(R.string.pending);
            }
            title_TV.setText(title);
        }
    }
}
