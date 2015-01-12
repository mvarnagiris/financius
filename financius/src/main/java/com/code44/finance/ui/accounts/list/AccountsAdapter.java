package com.code44.finance.ui.accounts.list;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Currency;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.ThemeUtils;

public class AccountsAdapter extends ModelsAdapter<Account> {
    private final Currency mainCurrency;

    public AccountsAdapter(OnModelClickListener<Account> onModelClickListener, Currency mainCurrency) {
        super(onModelClickListener);
        this.mainCurrency = mainCurrency;
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_account, parent, false), mainCurrency);
    }

    @Override protected Account modelFromCursor(Cursor cursor) {
        return Account.from(cursor);
    }

    static class ViewHolder extends ModelViewHolder<Account> {
        private final TextView titleTextView;
        private final TextView balanceTextView;
        private final TextView mainCurrencyBalanceTextView;
        private final Currency mainCurrency;
        private final int includeInTotalsTextColor;
        private final int doNotIncludeInTotalsTextColor;

        public ViewHolder(View itemView, Currency mainCurrency) {
            super(itemView);
            this.mainCurrency = mainCurrency;
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            balanceTextView = (TextView) itemView.findViewById(R.id.balanceTextView);
            mainCurrencyBalanceTextView = (TextView) itemView.findViewById(R.id.mainCurrencyBalanceTextView);
            includeInTotalsTextColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorPrimary);
            doNotIncludeInTotalsTextColor = ThemeUtils.getColor(itemView.getContext(), android.R.attr.textColorSecondary);
        }

        @Override protected void bind(Account account, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            titleTextView.setText(account.getTitle());
            titleTextView.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
            balanceTextView.setText(MoneyFormatter.format(account.getCurrency(), account.getBalance()));
            balanceTextView.setTextColor(account.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
            if (account.getCurrency().getId().equals(mainCurrency.getId())) {
                mainCurrencyBalanceTextView.setVisibility(View.GONE);
            } else {
                mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
                mainCurrencyBalanceTextView.setText(MoneyFormatter.format(mainCurrency, (long) (account.getBalance() * account.getCurrency().getExchangeRate())));
            }
        }
    }
}
