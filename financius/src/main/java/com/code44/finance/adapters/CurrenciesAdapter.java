package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.db.model.Currency;

import nl.qbusict.cupboard.CupboardFactory;

public class CurrenciesAdapter extends CursorAdapter {
    public CurrenciesAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_currency, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Currency currency = CupboardFactory.cupboard().withCursor(cursor).get(Currency.class);
        holder.code_TV.setText(currency.getCode());
    }

    private static class ViewHolder {
        public TextView code_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.code_TV = (TextView) view.findViewById(R.id.code_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
