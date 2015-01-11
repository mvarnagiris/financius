package com.code44.finance.ui.currencies.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.ThemeUtils;

class CurrenciesAdapter extends ModelsAdapter<Currency> {
    public CurrenciesAdapter(OnModelClickListener<Currency> onModelClickListener) {
        super(onModelClickListener);
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_currency, parent, false));
    }

    @Override protected Currency modelFromCursor(Cursor cursor) {
        return Currency.from(cursor);
    }

    private static class ViewHolder extends ModelViewHolder<Currency> {
        public final TextView codeTextView;
        public final TextView formatTextView;
        public final TextView exchangeRateTextView;

        private final int textPrimaryColor;
        private final int textBrandColor;

        public ViewHolder(View itemView) {
            super(itemView);
            codeTextView = (TextView) itemView.findViewById(R.id.codeTextView);
            formatTextView = (TextView) itemView.findViewById(R.id.formatTextView);
            exchangeRateTextView = (TextView) itemView.findViewById(R.id.exchangeRateTextView);

            textPrimaryColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            textBrandColor = ThemeUtils.getColor(itemView.getContext(), R.attr.colorPrimary);
        }

        @Override protected void bind(Currency currency, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            codeTextView.setText(currency.getCode());
            formatTextView.setText(MoneyFormatter.format(currency, 100000));
            if (currency.isDefault()) {
                codeTextView.setTextColor(textBrandColor);
                exchangeRateTextView.setText(null);
            } else {
                codeTextView.setTextColor(textPrimaryColor);
                exchangeRateTextView.setText(String.valueOf(currency.getExchangeRate()));
            }
        }
    }
}
