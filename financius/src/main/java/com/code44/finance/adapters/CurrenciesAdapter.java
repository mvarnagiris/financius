package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.utils.MoneyFormatter;

public class CurrenciesAdapter extends BaseModelsAdapter {
    private final int textPrimaryColor;
    private final int textAccentColor;

    public CurrenciesAdapter(Context context) {
        super(context);
        textPrimaryColor = context.getResources().getColor(R.color.text_primary);
        textAccentColor = context.getResources().getColor(R.color.text_accent);
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
        final Currency currency = Currency.from(cursor);
        holder.code_TV.setText(currency.getCode());
        holder.format_TV.setText("(" + MoneyFormatter.format(currency, 100000) + ")");
        if (currency.isDefault()) {
            holder.code_TV.setTextColor(textAccentColor);
            holder.exchangeRate_TV.setText(null);
        } else {
            holder.code_TV.setTextColor(textPrimaryColor);
            holder.exchangeRate_TV.setText(String.valueOf(currency.getExchangeRate()));
        }
    }

    private static class ViewHolder {
        public TextView code_TV;
        public TextView format_TV;
        public TextView exchangeRate_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.code_TV = (TextView) view.findViewById(R.id.code_TV);
            holder.format_TV = (TextView) view.findViewById(R.id.format_TV);
            holder.exchangeRate_TV = (TextView) view.findViewById(R.id.exchangeRate_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
