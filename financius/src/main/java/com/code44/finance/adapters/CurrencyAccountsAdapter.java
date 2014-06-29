package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.model.Account;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;

public class CurrencyAccountsAdapter extends BaseModelsAdapter {
    private Currency currency;
    private final View.OnClickListener changeCurrencyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Account account = (Account) v.getTag();
            account.setCurrency(currency);
            DataStore.insert().model(account).into(AccountsProvider.uriAccounts());
        }
    };

    public CurrencyAccountsAdapter(Context context) {
        super(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_currency_account, parent, false);
        ViewHolder.setAsTag(view).currency_B.setOnClickListener(changeCurrencyClickListener);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Account account = Account.from(cursor);
        holder.title_TV.setText(account.getTitle());
        if (currency.getId() == account.getCurrency().getId()) {
            holder.currency_B.setVisibility(View.INVISIBLE);
        } else {
            holder.currency_B.setTag(account);
            holder.currency_B.setVisibility(View.VISIBLE);
            holder.currency_B.setText(mContext.getString(R.string.f_change_to_x, currency.getCode()));
        }
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    private static class ViewHolder {
        public TextView title_TV;
        public Button currency_B;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            holder.currency_B = (Button) view.findViewById(R.id.currency_B);
            view.setTag(holder);

            return holder;
        }
    }
}
