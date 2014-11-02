package com.code44.finance.ui.accounts;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.utils.MoneyFormatter;

public class AccountsAdapter extends BaseModelsAdapter {
    private final Currency defaultCurrency;
    private final int includeInTotalsTextColor;
    private final int doNotIncludeInTotalsTextColor;

    public AccountsAdapter(Context context, Currency defaultCurrency) {
        super(context);
        this.defaultCurrency = defaultCurrency;
        includeInTotalsTextColor = context.getResources().getColor(R.color.text_primary);
        doNotIncludeInTotalsTextColor = context.getResources().getColor(R.color.text_secondary);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_account, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Account account = Account.from(cursor);
        holder.title_TV.setText(account.getTitle());
        holder.balance_TV.setText(MoneyFormatter.format(account.getCurrency(), account.getBalance()));
        holder.balance_TV.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
        if (account.getCurrency().getId().equals(defaultCurrency.getId())) {
            holder.mainCurrencyBalance_TV.setVisibility(View.GONE);
        } else {
            holder.mainCurrencyBalance_TV.setVisibility(View.VISIBLE);
            holder.mainCurrencyBalance_TV.setText(MoneyFormatter.format(defaultCurrency, (long) (account.getBalance() * account.getCurrency().getExchangeRate())));
        }
    }

    private static class ViewHolder {
        public TextView title_TV;
        public TextView balance_TV;
        public TextView mainCurrencyBalance_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.titleTextView);
            holder.balance_TV = (TextView) view.findViewById(R.id.balanceTextView);
            holder.mainCurrencyBalance_TV = (TextView) view.findViewById(R.id.mainCurrencyBalanceTextView);
            view.setTag(holder);

            return holder;
        }
    }
}
