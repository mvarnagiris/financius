package com.code44.finance.ui.currencies.list;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelViewHolder;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.utils.ThemeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

class CurrenciesAdapter extends ModelsAdapter<CurrencyFormat, CurrenciesAdapter.ViewHolder> {
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;
    private final int textPrimaryColor;
    private final int textBrandColor;

    public CurrenciesAdapter(@NonNull OnModelClickListener<CurrencyFormat> onModelClickListener, @NonNull ModelsActivity.Mode mode, @NonNull Context context, @NonNull CurrenciesManager currenciesManager, @NonNull AmountFormatter amountFormatter) {
        super(onModelClickListener, mode);
        checkNotNull(context, "Context cannot be null");
        this.currenciesManager = checkNotNull(currenciesManager, "CurrenciesManager cannot be null.");
        this.amountFormatter = checkNotNull(amountFormatter, "AmountFormatter cannot be null.");
        textPrimaryColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        textBrandColor = ThemeUtils.getColor(context, R.attr.colorPrimary);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_currency, parent, false), this);
    }

    @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull CurrencyFormat model, boolean isSelected) {
        holder.codeTextView.setText(model.getCode());
        holder.formatTextView.setText(amountFormatter.format(model.getCode(), 100000));
        if (currenciesManager.isMainCurrency(model.getCode())) {
            holder.codeTextView.setTextColor(textBrandColor);
            holder.exchangeRateTextView.setText(null);
        } else {
            holder.codeTextView.setTextColor(textPrimaryColor);
            holder.exchangeRateTextView.setText(String.valueOf(currenciesManager.getExchangeRate(model.getCode(), currenciesManager.getMainCurrencyCode())));
        }
    }

    @Override protected CurrencyFormat modelFromCursor(Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    static class ViewHolder extends ModelViewHolder {
        private final TextView codeTextView;
        private final TextView formatTextView;
        private final TextView exchangeRateTextView;

        public ViewHolder(@NonNull View itemView, @NonNull OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            codeTextView = (TextView) itemView.findViewById(R.id.codeTextView);
            formatTextView = (TextView) itemView.findViewById(R.id.formatTextView);
            exchangeRateTextView = (TextView) itemView.findViewById(R.id.exchangeRateTextView);
        }
    }
}
