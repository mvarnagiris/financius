package com.code44.finance.ui.currencies;

import android.content.Context;
import android.database.Cursor;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.ui.common.BaseModelsAdapter;

public class CurrencyAccountsAdapter extends BaseModelsAdapter {
    private final int textBrandColor;

    private Currency currency;

    private final View.OnClickListener changeCurrencyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Account account = (Account) v.getTag();
            account.setCurrency(currency);
            DataStore.insert().model(account).into(mContext, AccountsProvider.uriAccounts());
        }
    };

    public CurrencyAccountsAdapter(Context context) {
        super(context);
        textBrandColor = context.getResources().getColor(R.color.text_brand);
    }

    @Override public int getCount() {
        return currency == null || Strings.isEmpty(currency.getCode()) ? 0 : super.getCount();
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_currency_account, parent, false);
        ViewHolder.setAsTag(view).currencyButton.setOnClickListener(changeCurrencyClickListener);
        return view;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Account account = Account.from(cursor);

        final String accountText = account.getTitle() + ", " + account.getCurrency().getCode();
        if (currency.getId().equals(account.getCurrency().getId())) {
            holder.currencyButton.setVisibility(View.INVISIBLE);
            final SpannableStringBuilder ssb = new SpannableStringBuilder(accountText);
            ssb.setSpan(new ForegroundColorSpan(textBrandColor), ssb.length() - account.getCurrency().getCode().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.titleTextView.setText(ssb);
        } else {
            holder.titleTextView.setText(accountText);
            holder.currencyButton.setTag(account);
            holder.currencyButton.setVisibility(View.VISIBLE);

            final String text = mContext.getString(R.string.f_change_to_x, currency.getCode()).toUpperCase();
            final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            final int codeIndex = text.indexOf(currency.getCode().toUpperCase());
            if (codeIndex > 0) {
                ssb.setSpan(new ForegroundColorSpan(textBrandColor), codeIndex, codeIndex + currency.getCode().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            holder.currencyButton.setText(ssb);
        }
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView titleTextView;
        public Button currencyButton;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            holder.currencyButton = (Button) view.findViewById(R.id.currencyButton);
            holder.currencyButton.setAllCaps(false);
            view.setTag(holder);

            return holder;
        }
    }
}
