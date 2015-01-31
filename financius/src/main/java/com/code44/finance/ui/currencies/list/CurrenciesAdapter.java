package com.code44.finance.ui.currencies.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.ThemeUtils;

class CurrenciesAdapter extends ModelsAdapter<CurrencyFormat> {
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    public CurrenciesAdapter(OnModelClickListener<CurrencyFormat> onModelClickListener, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(onModelClickListener);
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_currency, parent, false), currenciesManager, amountFormatter);
    }

    @Override protected CurrencyFormat modelFromCursor(Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    private static class ViewHolder extends ModelViewHolder<CurrencyFormat> {
        private final TextView codeTextView;
        private final TextView formatTextView;
        private final TextView exchangeRateTextView;

        private final CurrenciesManager currenciesManager;
        private final AmountFormatter amountFormatter;
        private final int textPrimaryColor;
        private final int textBrandColor;

        public ViewHolder(View itemView, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
            super(itemView);
            this.currenciesManager = currenciesManager;
            this.amountFormatter = amountFormatter;

            codeTextView = (TextView) itemView.findViewById(R.id.codeTextView);
            formatTextView = (TextView) itemView.findViewById(R.id.formatTextView);
            exchangeRateTextView = (TextView) itemView.findViewById(R.id.exchangeRateTextView);

            textPrimaryColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            textBrandColor = ThemeUtils.getColor(itemView.getContext(), R.attr.colorPrimary);
        }

        @Override protected void bind(CurrencyFormat currencyFormat, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            codeTextView.setText(currencyFormat.getCode());

            formatTextView.setText(amountFormatter.format(currencyFormat.getCode(), 100000));
            if (currenciesManager.isMainCurrency(currencyFormat.getCode())) {
                codeTextView.setTextColor(textBrandColor);
                exchangeRateTextView.setText(null);
            } else {
                codeTextView.setTextColor(textPrimaryColor);
                exchangeRateTextView.setText(String.valueOf(currenciesManager.getExchangeRate(currencyFormat.getCode(), currenciesManager.getMainCurrencyCode())));
            }
        }
    }
}
