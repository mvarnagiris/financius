package com.code44.finance.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.utils.MoneyFormatter;

public class CurrenciesAdapterV2 extends RecyclerView.Adapter<CurrenciesAdapterV2.ViewHolder> {
    private Cursor cursor;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_currency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        cursor.moveToPosition(position);
        viewHolder.setCurrency(Currency.from(cursor));
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        final Cursor oldCursor = this.cursor;
        this.cursor = cursor;
        notifyDataSetChanged();

        return oldCursor;
    }

    protected static final class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView code_TV;
        private final TextView format_TV;
        private final TextView exchangeRate_TV;

        private final int textPrimaryColor;
        private final int textAccentColor;

        public ViewHolder(View itemView) {
            super(itemView);

            code_TV = (TextView) itemView.findViewById(R.id.code_TV);
            format_TV = (TextView) itemView.findViewById(R.id.format_TV);
            exchangeRate_TV = (TextView) itemView.findViewById(R.id.exchangeRate_TV);

            textPrimaryColor = code_TV.getCurrentTextColor();
            textAccentColor = itemView.getResources().getColor(R.color.text_accent);

            itemView.setBackgroundResource(R.color.bg_window);
        }

        public void setCurrency(Currency currency) {
            code_TV.setText(currency.getCode());
            format_TV.setText(MoneyFormatter.format(currency, 100000));
            if (currency.isDefault()) {
                code_TV.setTextColor(textAccentColor);
                exchangeRate_TV.setText(null);
                itemView.setElevation(itemView.getContext().getResources().getDimension(R.dimen.elevation_bottom_bar));
            } else {
                code_TV.setTextColor(textPrimaryColor);
                exchangeRate_TV.setText(String.valueOf(currency.getExchangeRate()));
                itemView.setElevation(0);
            }
        }
    }
}
