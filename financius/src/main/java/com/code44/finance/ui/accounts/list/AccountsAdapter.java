package com.code44.finance.ui.accounts.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.ThemeUtils;

public class AccountsAdapter extends ModelsAdapter<Account> {
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;

    public AccountsAdapter(OnModelClickListener<Account> onModelClickListener, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
        super(onModelClickListener);
        this.currenciesManager = currenciesManager;
        this.amountFormatter = amountFormatter;
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_account, parent, false), currenciesManager, amountFormatter);
    }

    @Override protected Account modelFromCursor(Cursor cursor) {
        return Account.from(cursor);
    }

    static class ViewHolder extends ModelViewHolder<Account> {
        private final TextView titleTextView;
        private final TextView balanceTextView;
        private final TextView mainCurrencyBalanceTextView;

        private final CurrenciesManager currenciesManager;
        private final AmountFormatter amountFormatter;
        private final int includeInTotalsTextColor;
        private final int doNotIncludeInTotalsTextColor;

        public ViewHolder(View itemView, CurrenciesManager currenciesManager, AmountFormatter amountFormatter) {
            super(itemView);
            this.currenciesManager = currenciesManager;
            this.amountFormatter = amountFormatter;
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            balanceTextView = (TextView) itemView.findViewById(R.id.balanceTextView);
            mainCurrencyBalanceTextView = (TextView) itemView.findViewById(R.id.mainCurrencyBalanceTextView);
            includeInTotalsTextColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            doNotIncludeInTotalsTextColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorSecondary);
        }

        @Override protected void bind(Account account, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            titleTextView.setText(account.getTitle());
            titleTextView.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
            balanceTextView.setText(amountFormatter.format(account.getCurrencyCode(), account.getBalance()));
            balanceTextView.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
            if (account.getCurrencyCode().equals(currenciesManager.getMainCurrencyCode())) {
                mainCurrencyBalanceTextView.setVisibility(View.GONE);
            } else {
                mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
                mainCurrencyBalanceTextView.setText(amountFormatter.format(currenciesManager.getMainCurrencyCode(), (long) (account.getBalance() * currenciesManager.getExchangeRate(account.getCurrencyCode(), currenciesManager.getMainCurrencyCode()))));
            }
        }
    }
}
