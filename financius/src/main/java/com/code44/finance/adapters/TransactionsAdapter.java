package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.MoneyFormatter;

import org.joda.time.DateTime;

public class TransactionsAdapter extends BaseModelsAdapter {
    private final int expenseColor;
    private final int incomeColor;
    private final int transferColor;

    public TransactionsAdapter(Context context) {
        super(context);
        expenseColor = context.getResources().getColor(R.color.text_primary);
        incomeColor = context.getResources().getColor(R.color.text_positive);
        transferColor = context.getResources().getColor(R.color.text_neutral);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_transaction, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
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
}
