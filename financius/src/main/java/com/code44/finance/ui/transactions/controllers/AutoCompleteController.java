package com.code44.finance.ui.transactions.controllers;

import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;

public interface AutoCompleteController<T> {
    public AutoCompleteAdapter<T> show(AutoCompleteAdapter<?> currentAdapter, AutoCompleteResult autoCompleteResult, AutoCompleteAdapter.OnAutoCompleteItemClickListener<T> clickListener);
}
