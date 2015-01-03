package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.ThemeUtils;

public class CurrenciesAdapter extends BaseModelsAdapter {
    private final int textPrimaryColor;
    private final int textBrandColor;

    public CurrenciesAdapter(Context context) {
        super(context);
        textPrimaryColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        textBrandColor = ThemeUtils.getColor(context, R.attr.colorPrimary);
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
        holder.codeTextView.setText(currency.getCode());
        holder.formatTextView.setText(MoneyFormatter.format(currency, 100000));
        if (currency.isDefault()) {
            holder.codeTextView.setTextColor(textBrandColor);
            holder.exchangeRateTextView.setText(null);
        } else {
            holder.codeTextView.setTextColor(textPrimaryColor);
            holder.exchangeRateTextView.setText(String.valueOf(currency.getExchangeRate()));
        }
    }

    private static class ViewHolder {
        public TextView codeTextView;
        public TextView formatTextView;
        public TextView exchangeRateTextView;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.codeTextView = (TextView) view.findViewById(R.id.codeTextView);
            holder.formatTextView = (TextView) view.findViewById(R.id.formatTextView);
            holder.exchangeRateTextView = (TextView) view.findViewById(R.id.exchangeRateTextView);
            view.setTag(holder);

            return holder;
        }
    }
}
