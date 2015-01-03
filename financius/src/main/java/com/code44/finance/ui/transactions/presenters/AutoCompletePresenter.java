package com.code44.finance.ui.transactions.presenters;

import android.view.View;

import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;

public interface AutoCompletePresenter<T> {
    public AutoCompleteAdapter<T> showAutoComplete(AutoCompleteAdapter<?> currentAdapter, TransactionEditData transactionEditData, AutoCompleteAdapter.OnAutoCompleteItemClickListener<T> clickListener, View view);
}
