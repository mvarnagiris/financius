package com.code44.finance.ui.transactions.edit.autocomplete.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.edit.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.edit.presenters.TransactionEditData;

import java.util.List;

public class AutoCompleteAccountsToAdapter extends AutoCompleteAdapter<Account> {
    public AutoCompleteAccountsToAdapter(ViewGroup containerView, AutoCompleteAdapterListener listener, OnAutoCompleteItemClickListener<Account> clickListener) {
        super(containerView, listener, clickListener);
    }

    @Override protected View newView(Context context, ViewGroup containerView) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_account, containerView, false);
        final int keylineContent = context.getResources().getDimensionPixelSize(R.dimen.keyline_content);
        view.setPadding(keylineContent, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
        return view;
    }

    @Override protected void bindView(View view, Account account) {
        ((TextView) view.findViewById(R.id.titleTextView)).setText(account.getTitle());
    }

    @Override protected boolean isSameAdapter(AutoCompleteAdapter<?> currentAdapter) {
        return currentAdapter instanceof AutoCompleteAccountsToAdapter;
    }

    @Override protected boolean showItem(TransactionEditData transactionEditData, Account item) {
        return item != null && !item.equals(transactionEditData.getAccountTo());
    }

    @Override protected List<Account> getItems(AutoCompleteResult autoCompleteResult) {
        return autoCompleteResult.getAccountsTo();
    }
}
