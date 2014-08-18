package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.IntervalHelper;
import com.code44.finance.utils.MoneyFormatter;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class TransactionsAdapter extends BaseModelsAdapter implements StickyListHeadersAdapter {
    private final IntervalHelper intervalHelper;
    private final LongSparseArray<Long> totalExpenses;
    private final int expenseColor;
    private final int incomeColor;
    private final int transferColor;

    public TransactionsAdapter(Context context, IntervalHelper intervalHelper) {
        super(context);

        this.intervalHelper = intervalHelper;
        totalExpenses = new LongSparseArray<>();
        expenseColor = context.getResources().getColor(R.color.text_primary);
        incomeColor = context.getResources().getColor(R.color.text_positive);
        transferColor = context.getResources().getColor(R.color.text_neutral);
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
        holder.color_IV.setColorFilter(category.getColor());
        holder.weekday_TV.setText(date.dayOfWeek().getAsShortText());
        holder.day_TV.setText(date.dayOfMonth().getAsShortText());
        holder.category_TV.setText(category.getTitle());
        holder.note_TV.setText(transaction.getNote());
        holder.amount_TV.setText(MoneyFormatter.format(transaction));

        if (category.getCategoryType() == CategoryType.EXPENSE) {
            holder.account_TV.setText(transaction.getAccountFrom().getTitle());
            holder.amount_TV.setTextColor(expenseColor);
        } else if (category.getCategoryType() == CategoryType.INCOME) {
            holder.account_TV.setText(transaction.getAccountTo().getTitle());
            holder.amount_TV.setTextColor(incomeColor);
        } else {
            holder.account_TV.setText(transaction.getAccountFrom().getTitle() + " > " + transaction.getAccountTo().getTitle());
            holder.amount_TV.setTextColor(transferColor);
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

        final long date = getCursor().getLong(getCursor().getColumnIndex(Tables.Transactions.DATE.getName()));
        final Period period = IntervalHelper.getPeriod(intervalHelper.getIntervalLength(), intervalHelper.getType());
        final Interval interval = IntervalHelper.getInterval(date, period, intervalHelper.getType());
        final String title = IntervalHelper.getIntervalTitle(mContext, interval, intervalHelper.getType());
        holder.title_TV.setText(title);
        holder.amount_TV.setText(MoneyFormatter.format(Currency.getDefault(), getTotalExpenseForPosition(position)));

        return convertView;
    }

    @Override public long getHeaderId(int position) {
        getCursor().moveToPosition(position);
        final long date = getCursor().getLong(getCursor().getColumnIndex(Tables.Transactions.DATE.getName()));
        final Period period = IntervalHelper.getPeriod(intervalHelper.getIntervalLength(), intervalHelper.getType());
        final Interval interval = IntervalHelper.getInterval(date, period, intervalHelper.getType());
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
        final int iAccountFromCurrencyServerId = cursor.getColumnIndex(Tables.Currencies.SERVER_ID.getName(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY));
        final int iAccountFromCurrencyExchangeRate = cursor.getColumnIndex(Tables.Currencies.EXCHANGE_RATE.getName(Tables.Currencies.TEMP_TABLE_NAME_FROM_CURRENCY));
        do {
            if (CategoryType.fromInt(cursor.getInt(iCategoryType)) == CategoryType.EXPENSE) {
                final long amount = cursor.getLong(iAmount);
                if (Currency.getDefault().getServerId().equals(cursor.getString(iAccountFromCurrencyServerId))) {
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
        public TextView category_TV;
        public TextView note_TV;
        public TextView amount_TV;
        public TextView account_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.color_IV = (ImageView) view.findViewById(R.id.color_IV);
            holder.weekday_TV = (TextView) view.findViewById(R.id.weekday_TV);
            holder.day_TV = (TextView) view.findViewById(R.id.day_TV);
            holder.category_TV = (TextView) view.findViewById(R.id.category_TV);
            holder.note_TV = (TextView) view.findViewById(R.id.note_TV);
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
