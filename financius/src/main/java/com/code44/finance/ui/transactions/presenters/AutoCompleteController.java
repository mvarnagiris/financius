package com.code44.finance.ui.transactions.presenters;

import android.view.View;

import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;

public interface AutoCompleteController<T> {
    public AutoCompleteAdapter<T> showAutoComplete(AutoCompleteAdapter<?> currentAdapter, AutoCompleteResult autoCompleteResult, AutoCompleteAdapter.OnAutoCompleteItemClickListener<T> clickListener, View view);
}
