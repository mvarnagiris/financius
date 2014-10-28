package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.utils.MoneyFormatter;

public class CurrenciesAdapter extends BaseModelsAdapter<Currency, CurrenciesAdapter.CurrencyHolder> {
    public CurrenciesAdapter(Context context) {
        super(context);
    }

    @Override protected CurrencyHolder createViewHolder(Context context, ViewGroup parent, int position) {
        return new CurrencyHolder(context, LayoutInflater.from(context).inflate(R.layout.li_currency, parent, false));
    }

    @Override protected void bindViewHolder(Context context, CurrencyHolder holder, int position, Currency model) {
        holder.bind(model);
    }

    @Override protected Currency createModel(Cursor cursor) {
        return Currency.from(cursor);
    }

    public static class CurrencyHolder extends RecyclerView.ViewHolder {
        private final TextView codeView;
        private final TextView formatView;
        private final TextView exchangeRateView;

        private final int textPrimaryColor;
        private final int textBrandColor;

        public CurrencyHolder(Context context, View view) {
            super(view);
            codeView = (TextView) view.findViewById(R.id.code);
            formatView = (TextView) view.findViewById(R.id.format);
            exchangeRateView = (TextView) view.findViewById(R.id.exchangeRate);

            textPrimaryColor = context.getResources().getColor(R.color.text_primary);
            textBrandColor = context.getResources().getColor(R.color.text_brand);
        }

        public void bind(Currency currency) {
            codeView.setText(currency.getCode());
            formatView.setText(MoneyFormatter.format(currency, 100000));
            if (currency.isDefault()) {
                codeView.setTextColor(textBrandColor);
                exchangeRateView.setText(null);
            } else {
                codeView.setTextColor(textPrimaryColor);
                exchangeRateView.setText(String.valueOf(currency.getExchangeRate()));
            }
        }
    }
}
