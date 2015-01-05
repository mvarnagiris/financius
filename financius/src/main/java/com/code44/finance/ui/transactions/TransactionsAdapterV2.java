package com.code44.finance.ui.transactions;

import android.content.Context;
import android.database.Cursor;
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
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsPresenter;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.TextBackgroundSpan;
import com.code44.finance.utils.ThemeUtils;

import org.joda.time.DateTime;

public class TransactionsAdapterV2 extends ModelsAdapter<Transaction> {
    private final Currency mainCurrency;
    private final BaseInterval interval;

    public TransactionsAdapterV2(Currency mainCurrency, BaseInterval interval) {
        // TODO This mst be click listener
        super(null);
        this.mainCurrency = mainCurrency;
        this.interval = interval;
    }

    @Override public ModelsAdapter.ViewHolder<Transaction> createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_transaction, parent, false), mainCurrency, interval);
    }

    @Override protected Transaction modelFromCursor(Cursor cursor) {
        return Transaction.from(cursor);
    }

    private class ViewHolder extends ModelsAdapter.ViewHolder<Transaction> {
        private static final String UNKNOWN_VALUE = "?";
        private static final String TRANSFER_SYMBOL = " → ";

        private final ImageView colorImageView;
        private final TextView weekdayTextView;
        private final TextView dayTextView;
        private final TextView titleTextView;
        private final TextView subtitleTextView;
        private final TextView amountTextView;
        private final TextView accountTextView;

        private final Currency mainCurrency;
        private final BaseInterval interval;
        private final String unknownExpenseTitle;
        private final String unknownIncomeTitle;
        private final String unknownTransferTitle;
        private final int unknownExpenseColor;
        private final int unknownIncomeColor;
        private final int unknownTransferColor;
        private final int expenseAmountColor;
        private final int incomeAmountColor;
        private final int transferAmountColor;
        private final int primaryColor;
        private final int weakColor;
        private final int tagBackgroundColor;
        private final float tagBackgroundRadius;

        public ViewHolder(View itemView, Currency mainCurrency, BaseInterval interval) {
            super(itemView);
            this.mainCurrency = mainCurrency;
            this.interval = interval;

            // Init
            final Context context = itemView.getContext();
            unknownExpenseTitle = context.getString(R.string.expense);
            unknownIncomeTitle = context.getString(R.string.income);
            unknownTransferTitle = context.getString(R.string.transfer);
            expenseAmountColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
            incomeAmountColor = ThemeUtils.getColor(context, R.attr.textColorPositive);
            transferAmountColor = ThemeUtils.getColor(context, R.attr.textColorNeutral);
            unknownExpenseColor = ThemeUtils.getColor(context, R.attr.textColorNegative);
            unknownIncomeColor = ThemeUtils.getColor(context, R.attr.textColorPositive);
            unknownTransferColor = ThemeUtils.getColor(context, R.attr.textColorNeutral);
            primaryColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
            weakColor = ThemeUtils.getColor(context, android.R.attr.textColorSecondary);
            tagBackgroundColor = ThemeUtils.getColor(context, R.attr.backgroundColorSecondary);
            tagBackgroundRadius = context.getResources().getDimension(R.dimen.tag_radius);

            // Get views
            colorImageView = (ImageView) itemView.findViewById(R.id.colorImageView);
            weekdayTextView = (TextView) itemView.findViewById(R.id.weekdayTextView);
            dayTextView = (TextView) itemView.findViewById(R.id.dayTextView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            subtitleTextView = (TextView) itemView.findViewById(R.id.subtitleTextView);
            amountTextView = (TextView) itemView.findViewById(R.id.amountTextView);
            accountTextView = (TextView) itemView.findViewById(R.id.accountTextView);
        }

        @Override public void bind(Transaction transaction, Cursor cursor, int position, ModelsPresenter.Mode mode, boolean isSelected) {
            final DateTime date = new DateTime(transaction.getDate());

            // Set values
            weekdayTextView.setText(date.dayOfWeek().getAsShortText());
            dayTextView.setText(date.dayOfMonth().getAsShortText());
            colorImageView.setColorFilter(getCategoryColor(transaction));
            amountTextView.setText(MoneyFormatter.format(transaction));
            titleTextView.setTextColor(primaryColor);
            titleTextView.setText(getTitle(transaction));
            subtitleTextView.setText(getSubtitle(transaction));
            bindViewForTransactionType(transaction);

            if (transaction.getTransactionState() == TransactionState.Pending) {
                bindViewPending(transaction);
            }
        }

        private String getTitle(Transaction transaction) {
            if (Strings.isEmpty(transaction.getNote())) {
                if (transaction.getCategory() != null && transaction.getCategory().hasId()) {
                    return transaction.getCategory().getTitle();
                } else {
                    switch (transaction.getTransactionType()) {
                        case Expense:
                            return unknownExpenseTitle;
                        case Income:
                            return unknownIncomeTitle;
                        case Transfer:
                            return unknownTransferTitle;
                        default:
                            throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
                    }
                }
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

        private int getCategoryColor(Transaction transaction) {
            if (transaction.getCategory() == null) {
                switch (transaction.getTransactionType()) {
                    case Expense:
                        return unknownExpenseColor;
                    case Income:
                        return unknownIncomeColor;
                    case Transfer:
                        return unknownTransferColor;
                    default:
                        throw new IllegalArgumentException("Transaction type " + transaction.getTransactionType() + " is not supported.");
                }
            } else {
                return transaction.getCategory().getColor();
            }
        }

        private void bindViewForTransactionType(Transaction transaction) {
            final String account;
            final int amountColor;
            switch (transaction.getTransactionType()) {
                case Expense:
                    account = transaction.getAccountFrom() != null ? transaction.getAccountFrom().getTitle() : UNKNOWN_VALUE;
                    amountColor = expenseAmountColor;
                    break;
                case Income:
                    account = transaction.getAccountTo() != null ? transaction.getAccountTo().getTitle() : UNKNOWN_VALUE;
                    amountColor = incomeAmountColor;
                    break;
                case Transfer:
                    account = (transaction.getAccountFrom() != null ? transaction.getAccountFrom().getTitle() : UNKNOWN_VALUE) + TRANSFER_SYMBOL + (transaction.getAccountTo() != null ? transaction.getAccountTo().getTitle() : UNKNOWN_VALUE);
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
    }
}
