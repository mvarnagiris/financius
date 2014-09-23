package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableStringBuilder;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.AccountOwner;
import com.code44.finance.common.model.CategoryOwner;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.common.model.TransactionState;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.BaseInterval;
import com.code44.finance.utils.CurrentInterval;
import com.code44.finance.utils.IntervalHelperDeprecated;
import com.code44.finance.utils.MoneyFormatter;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class TransactionsAdapter extends BaseModelsAdapter implements StickyListHeadersAdapter {
    private static final String UNKNOWN_VALUE = "?";
    private static final String TRANSFER_SYMBOL = " > ";

    private final Currency defaultCurrency;
    private final Category transferCategory;
    private final BaseInterval interval;
    private final LongSparseArray<Long> totalExpenses;
    private final int expenseColor;
    private final int incomeColor;
    private final int transferColor;
    private final int primaryColor;
    private final int weakColor;

    public TransactionsAdapter(Context context, Currency defaultCurrency, Category transferCategory, CurrentInterval interval) {
        super(context);
        this.defaultCurrency = defaultCurrency;
        this.transferCategory = transferCategory;
        this.interval = interval;
        totalExpenses = new LongSparseArray<>();
        expenseColor = context.getResources().getColor(R.color.text_primary);
        incomeColor = context.getResources().getColor(R.color.text_positive);
        transferColor = context.getResources().getColor(R.color.text_neutral);
        primaryColor = context.getResources().getColor(R.color.text_primary);
        weakColor = context.getResources().getColor(R.color.text_weak);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_transaction, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Transaction transaction = Transaction.from(cursor);
        final Category category = transaction.getCategory();
        final DateTime date = new DateTime(transaction.getDate());
        final String note = transaction.getNote();
        final String title;
        final SpannableStringBuilder subtitle;

        // Prepare title
        if (StringUtils.isEmpty(note)) {
            title = category.getTitle();
        } else {
            title = note;
        }

        // Prepare subtitle
        if (transaction.getTags().size() > 0) {
            subtitle = new SpannableStringBuilder();
            for (Tag tag : transaction.getTags()) {
                if (subtitle.length() > 0) {
                    subtitle.append(" ");
                }
                subtitle.append("#").append(tag.getTitle());
            }
        } else {
            subtitle = null;
        }


        // Set values
        holder.weekday_TV.setText(date.dayOfWeek().getAsShortText());
        holder.day_TV.setText(date.dayOfMonth().getAsShortText());
        holder.color_IV.setColorFilter(category.getColor());
        holder.amount_TV.setText(MoneyFormatter.format(transaction));
        holder.title_TV.setTextColor(primaryColor);
        holder.title_TV.setText(title);
        holder.subtitle_TV.setText(subtitle);

        if (category.getCategoryType() == CategoryType.EXPENSE) {
            holder.account_TV.setText(transaction.getAccountFrom().getTitle());
            holder.amount_TV.setTextColor(expenseColor);
        } else if (category.getCategoryType() == CategoryType.INCOME) {
            holder.account_TV.setText(transaction.getAccountTo().getTitle());
            holder.amount_TV.setTextColor(incomeColor);
        } else {
            holder.account_TV.setText(transaction.getAccountFrom().getTitle() + TRANSFER_SYMBOL + transaction.getAccountTo().getTitle());
            holder.amount_TV.setTextColor(transferColor);
        }

        if (transaction.getTransactionState() == TransactionState.PENDING) {
            if (category.getCategoryOwner() == CategoryOwner.SYSTEM && !category.getId().equals(transferCategory.getId())) {
                holder.title_TV.setTextColor(weakColor);
                holder.color_IV.setColorFilter(weakColor);
            }

            if (transaction.getAmount() == 0) {
                holder.amount_TV.setTextColor(weakColor);
            }

            if (category.getCategoryType() == CategoryType.EXPENSE) {
                if (transaction.getAccountFrom().getAccountOwner() == AccountOwner.SYSTEM) {
                    holder.account_TV.setText(UNKNOWN_VALUE);
                }

                if (transaction.getAmount() > 0) {
                    holder.amount_TV.setTextColor(expenseColor);
                }
            } else if (category.getCategoryType() == CategoryType.INCOME) {
                if (transaction.getAccountTo().getAccountOwner() == AccountOwner.SYSTEM) {
                    holder.account_TV.setText(UNKNOWN_VALUE);
                }

                if (transaction.getAmount() > 0) {
                    holder.amount_TV.setTextColor(incomeColor);
                }
            } else {
                final String accountFrom = transaction.getAccountFrom().getAccountOwner() == AccountOwner.SYSTEM ? UNKNOWN_VALUE : transaction.getAccountFrom().getTitle();
                final String accountTo = transaction.getAccountTo().getAccountOwner() == AccountOwner.SYSTEM ? UNKNOWN_VALUE : transaction.getAccountTo().getTitle();
                holder.account_TV.setText(accountFrom + TRANSFER_SYMBOL + accountTo);
                if (transaction.getAmount() > 0) {
                    holder.amount_TV.setTextColor(transferColor);
                }
            }
        }
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent) {
        getCursor().moveToPosition(position);
        final HeaderViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.li_transaction_header, parent, false);
            holder = HeaderViewHolder.setAsTag(convertView);
            holder.arrow_IV.setColorFilter(holder.title_TV.getCurrentTextColor());
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        final String title;
        final TransactionState transactionState = TransactionState.fromInt(getCursor().getInt(getCursor().getColumnIndex(Tables.Transactions.STATE.getName())));
        if (transactionState == TransactionState.CONFIRMED) {
            final long date = getCursor().getLong(getCursor().getColumnIndex(Tables.Transactions.DATE.getName()));
            final Period period = IntervalHelperDeprecated.getPeriod(interval.getLength(), interval.getType());
            final Interval interval = IntervalHelperDeprecated.getInterval(date, period, this.interval.getType());
            title = IntervalHelperDeprecated.getIntervalTitle(mContext, interval, this.interval.getType());
        } else {
            title = mContext.getString(R.string.pending);
        }
        holder.title_TV.setText(title);
        holder.amount_TV.setText(MoneyFormatter.format(defaultCurrency, getTotalExpenseForPosition(position)));

        return convertView;
    }

    @Override public long getHeaderId(int position) {
        getCursor().moveToPosition(position);
        final TransactionState transactionState = TransactionState.fromInt(getCursor().getInt(getCursor().getColumnIndex(Tables.Transactions.STATE.getName())));
        if (transactionState == TransactionState.PENDING) {
            return 0;
        }

        final long date = getCursor().getLong(getCursor().getColumnIndex(Tables.Transactions.DATE.getName()));
        final Period period = IntervalHelperDeprecated.getPeriod(interval.getLength(), interval.getType());
        final Interval interval = IntervalHelperDeprecated.getInterval(date, period, this.interval.getType());
        return interval.getStartMillis();
    }

    @Override public Cursor swapCursor(Cursor newCursor) {
        totalExpenses.clear();
        return super.swapCursor(newCursor);
    }

    private long getTotalExpenseForPosition(int position) {
        final long headerId = getHeaderId(position);
        long totalExpense = totalExpenses.get(headerId, -1L);
        if (totalExpense != -1) {
            return totalExpense;
        }
        totalExpense = 0;

        final Cursor cursor = getCursor();
        final int iCategoryType = cursor.getColumnIndex(Tables.Categories.TYPE.getName());
        final int iAmount = cursor.getColumnIndex(Tables.Transactions.AMOUNT.getName());
        final int iAccountFromCurrencyServerId = cursor.getColumnIndex(Tables.Currencies.ID.getName(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY));
        final int iAccountFromCurrencyExchangeRate = cursor.getColumnIndex(Tables.Currencies.EXCHANGE_RATE.getName(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY));
        final int iIncludeInReports = cursor.getColumnIndex(Tables.Transactions.INCLUDE_IN_REPORTS.getName());
        do {
            if (CategoryType.fromInt(cursor.getInt(iCategoryType)) == CategoryType.EXPENSE && cursor.getInt(iIncludeInReports) != 0) {
                final long amount = cursor.getLong(iAmount);
                if (defaultCurrency.getId().equals(cursor.getString(iAccountFromCurrencyServerId))) {
                    totalExpense += amount;
                } else {
                    totalExpense += amount * cursor.getDouble(iAccountFromCurrencyExchangeRate);
                }
            }
        } while (cursor.moveToNext() && getHeaderId(cursor.getPosition()) == headerId);
        totalExpenses.put(headerId, totalExpense);
        return totalExpense;
    }

    private static class ViewHolder {
        public ImageView color_IV;
        public TextView weekday_TV;
        public TextView day_TV;
        public TextView title_TV;
        public TextView subtitle_TV;
        public TextView amount_TV;
        public TextView account_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.color_IV = (ImageView) view.findViewById(R.id.color_IV);
            holder.weekday_TV = (TextView) view.findViewById(R.id.weekday_TV);
            holder.day_TV = (TextView) view.findViewById(R.id.day_TV);
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            holder.subtitle_TV = (TextView) view.findViewById(R.id.subtitle_TV);
            holder.amount_TV = (TextView) view.findViewById(R.id.amount_TV);
            holder.account_TV = (TextView) view.findViewById(R.id.account_TV);
            view.setTag(holder);

            return holder;
        }
    }

    private static class HeaderViewHolder {
        public ImageView arrow_IV;
        public TextView title_TV;
        public TextView amount_TV;

        public static HeaderViewHolder setAsTag(View view) {
            final HeaderViewHolder holder = new HeaderViewHolder();
            holder.arrow_IV = (ImageView) view.findViewById(R.id.arrow_IV);
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            holder.amount_TV = (TextView) view.findViewById(R.id.amount_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
