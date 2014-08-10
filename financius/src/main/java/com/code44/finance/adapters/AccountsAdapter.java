package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.utils.MoneyFormatter;

public class AccountsAdapter extends BaseModelsAdapter {
    public AccountsAdapter(Context context) {
        super(context);
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
        if (account.getCurrency().getServerId().equals(Currency.getDefault().getServerId())) {
            holder.mainCurrencyBalance_TV.setVisibility(View.GONE);
        } else {
            holder.mainCurrencyBalance_TV.setVisibility(View.VISIBLE);
            holder.mainCurrencyBalance_TV.setText(MoneyFormatter.format(Currency.getDefault(), (long) (account.getBalance() * account.getCurrency().getExchangeRate())));
        }
    }

    private static class ViewHolder {
        public TextView title_TV;
        public TextView balance_TV;
        public TextView mainCurrencyBalance_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            holder.balance_TV = (TextView) view.findViewById(R.id.balance_TV);
            holder.mainCurrencyBalance_TV = (TextView) view.findViewById(R.id.mainCurrencyBalance_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
