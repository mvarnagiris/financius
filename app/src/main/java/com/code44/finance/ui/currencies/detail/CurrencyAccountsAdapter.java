package com.code44.finance.ui.currencies.detail;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.utils.ThemeUtils;
import com.google.common.base.Strings;

// TODO Move to recycler adapter
class CurrencyAccountsAdapter extends CursorAdapter {
    private final int textBrandColor;
    private CurrencyFormat currencyFormat;
    private final View.OnClickListener changeCurrencyClickListener = new View.OnClickListener() {
        @Override public void onClick(View v) {
            final Account account = (Account) v.getTag();
            account.setCurrencyCode(currencyFormat.getCode());
            DataStore.insert().model(account).into(mContext, AccountsProvider.uriAccounts());
        }
    };

    public CurrencyAccountsAdapter(Context context) {
        super(context, null, true);
        textBrandColor = ThemeUtils.getColor(context, R.attr.colorPrimary);
    }

    @Override public int getCount() {
        return currencyFormat == null || Strings.isNullOrEmpty(currencyFormat.getCode()) ? 0 : super.getCount();
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_currency_account, parent, false);
        ViewHolder.setAsTag(view).currencyButton.setOnClickListener(changeCurrencyClickListener);
        return view;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Account account = Account.from(cursor);

        final String accountText = account.getTitle() + ", " + account.getCurrencyCode();
        if (currencyFormat.getCode().equals(account.getCurrencyCode())) {
            holder.currencyButton.setVisibility(View.INVISIBLE);
            final SpannableStringBuilder ssb = new SpannableStringBuilder(accountText);
            ssb.setSpan(new ForegroundColorSpan(textBrandColor), ssb.length() - account.getCurrencyCode()
                    .length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.titleTextView.setText(ssb);
        } else {
            holder.titleTextView.setText(accountText);
            holder.currencyButton.setTag(account);
            holder.currencyButton.setVisibility(View.VISIBLE);

            final String text = mContext.getString(R.string.f_change_to_x, currencyFormat.getCode()).toUpperCase();
            final SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            final int codeIndex = text.indexOf(currencyFormat.getCode().toUpperCase());
            if (codeIndex > 0) {
                ssb.setSpan(new ForegroundColorSpan(textBrandColor), codeIndex, codeIndex + currencyFormat.getCode()
                        .length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            holder.currencyButton.setText(ssb);
        }
    }

    public void setCurrency(CurrencyFormat currencyFormat) {
        this.currencyFormat = currencyFormat;
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
