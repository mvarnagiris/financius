package com.code44.finance.ui.accounts.list;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.CurrenciesManager;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelViewHolder;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.utils.ThemeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

class AccountsAdapter extends ModelsAdapter<Account, AccountsAdapter.ViewHolder> {
    private final CurrenciesManager currenciesManager;
    private final AmountFormatter amountFormatter;
    private final int includeInTotalsTextColor;
    private final int doNotIncludeInTotalsTextColor;

    public AccountsAdapter(@NonNull OnModelClickListener<Account> onModelClickListener, @NonNull ModelsActivity.Mode mode, @NonNull Context context, @NonNull CurrenciesManager currenciesManager, @NonNull AmountFormatter amountFormatter) {
        super(onModelClickListener, mode);

        checkNotNull(context, "Context cannot be null.");
        this.currenciesManager = checkNotNull(currenciesManager, "CurrenciesManager cannot be null.");
        this.amountFormatter = checkNotNull(amountFormatter, "AmountFormatter cannot be null.");

        includeInTotalsTextColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        doNotIncludeInTotalsTextColor = ThemeUtils.getColor(context, android.R.attr.textColorSecondary);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_account, parent, false), this);
    }

    @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Account model, boolean isSelected) {
        holder.titleTextView.setText(model.getTitle());
        holder.titleTextView.setTextColor(model.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
        holder.balanceTextView.setText(amountFormatter.format(model.getCurrencyCode(), model.getBalance()));
        holder.balanceTextView.setTextColor(model.includeInTotals() ? includeInTotalsTextColor : doNotIncludeInTotalsTextColor);
        if (model.getCurrencyCode().equals(currenciesManager.getMainCurrencyCode())) {
            holder.mainCurrencyBalanceTextView.setVisibility(View.GONE);
        } else {
            holder.mainCurrencyBalanceTextView.setVisibility(View.VISIBLE);
            holder.mainCurrencyBalanceTextView.setText(amountFormatter.format(currenciesManager.getMainCurrencyCode(), (long) (model.getBalance() * currenciesManager
                    .getExchangeRate(model.getCurrencyCode(), currenciesManager.getMainCurrencyCode()))));
        }
    }

    @Override protected Account modelFromCursor(Cursor cursor) {
        return Account.from(cursor);
    }

    static class ViewHolder extends ModelViewHolder {
        private final TextView titleTextView;
        private final TextView balanceTextView;
        private final TextView mainCurrencyBalanceTextView;

        public ViewHolder(@NonNull View itemView, @NonNull OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            balanceTextView = (TextView) itemView.findViewById(R.id.balanceTextView);
            mainCurrencyBalanceTextView = (TextView) itemView.findViewById(R.id.mainCurrencyBalanceTextView);
        }
    }
}
