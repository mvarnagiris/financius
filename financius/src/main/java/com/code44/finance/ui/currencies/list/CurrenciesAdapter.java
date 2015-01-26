package com.code44.finance.ui.currencies.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.ThemeUtils;

class CurrenciesAdapter extends ModelsAdapter<CurrencyFormat> {
    public CurrenciesAdapter(OnModelClickListener<CurrencyFormat> onModelClickListener) {
        super(onModelClickListener);
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_currency, parent, false));
    }

    @Override protected CurrencyFormat modelFromCursor(Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    private static class ViewHolder extends ModelViewHolder<CurrencyFormat> {
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

        @Override protected void bind(CurrencyFormat currencyFormat, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            codeTextView.setText(currencyFormat.getCode());
// TODO            formatTextView.setText(MoneyFormatter.format(currencyFormat, 100000));
//            if (currencyFormat.isDefault()) {
//                codeTextView.setTextColor(textBrandColor);
//                exchangeRateTextView.setText(null);
//            } else {
//                codeTextView.setTextColor(textPrimaryColor);
//                exchangeRateTextView.setText(String.valueOf(currencyFormat.getExchangeRate()));
//            }
        }
    }
}
