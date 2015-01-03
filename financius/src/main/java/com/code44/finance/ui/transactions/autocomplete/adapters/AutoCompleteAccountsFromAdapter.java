package com.code44.finance.ui.transactions.autocomplete.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Account;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.presenters.TransactionEditData;

import java.util.List;

public class AutoCompleteAccountsFromAdapter extends AutoCompleteAdapter<Account> {
    public AutoCompleteAccountsFromAdapter(ViewGroup containerView, AutoCompleteAdapterListener listener, OnAutoCompleteItemClickListener<Account> clickListener) {
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
        return currentAdapter instanceof AutoCompleteAccountsFromAdapter;
    }

    @Override protected boolean showItem(TransactionEditData transactionEditData, Account item) {
        return item != null && !item.equals(transactionEditData.getAccountFrom());
    }

    @Override protected List<Account> getItems(AutoCompleteResult autoCompleteResult) {
        return autoCompleteResult.getAccountsFrom();
    }
}
