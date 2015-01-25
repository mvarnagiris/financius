package com.code44.finance.ui.accounts.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.ThemeUtils;

public class AccountsAdapter extends ModelsAdapter<Account> {
    private final CurrenciesManager currenciesManager;

    public AccountsAdapter(OnModelClickListener<Account> onModelClickListener, CurrenciesManager currenciesManager) {
        super(onModelClickListener);
        this.currenciesManager = currenciesManager;
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_account, parent, false), currenciesManager);
    }

    @Override protected Account modelFromCursor(Cursor cursor) {
        return Account.from(cursor);
    }

    static class ViewHolder extends ModelViewHolder<Account> {
        private final TextView titleTextView;
        private final TextView balanceTextView;
        private final TextView mainCurrencyBalanceTextView;
        private final CurrenciesManager currenciesManager;
        private final int includeInTotalsTextColor;
        private final int doNotIncludeInTotalsTextColor;

        public ViewHolder(View itemView, CurrenciesManager currenciesManager) {
            super(itemView);
            this.currenciesManager = currenciesManager;
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            balanceTextView = (TextView) itemView.findViewById(R.id.balanceTextView);
            mainCurrencyBalanceTextView = (TextView) itemView.findViewById(R.id.mainCurrencyBalanceTextView);
            includeInTotalsTextColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            doNotIncludeInTotalsTextColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorSecondary);
        }

        @Override protected void bind(Account account, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            titleTextView.setText(account.getTitle());
            titleTextView.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
            balanceTextView.setText(currenciesManager.formatMoney(account.getCurrencyCode(), account.getBalance()));
            balanceTextView.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
            if (account.getCurrencyCode().equals(currenciesManager.getMainCurrencyCode())) {
                mainCurrencyBalanceTextView.setVisibility(View.GONE);
            } else {
                mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
//           TODO     mainCurrencyBalanceTextView.setText(currenciesManager.formatMoney((long) (account.getBalance() * account.getCurrencyCode().getExchangeRate())));
            }
        }
    }
}
