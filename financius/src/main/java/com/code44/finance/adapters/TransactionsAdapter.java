package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Transaction;
import com.code44.finance.utils.MoneyFormatter;

public class TransactionsAdapter extends CursorAdapter {
    public TransactionsAdapter(Context context) {
        super(context, null, true);
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
        holder.amount_TV.setText(MoneyFormatter.format(transaction));
    }

    private static class ViewHolder {
        public TextView amount_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.amount_TV = (TextView) view.findViewById(R.id.amount_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
