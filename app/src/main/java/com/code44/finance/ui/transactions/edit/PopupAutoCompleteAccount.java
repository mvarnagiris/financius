package com.code44.finance.ui.transactions.edit;

import android.support.annotation.NonNull;
import android.view.View;

import com.code44.finance.data.model.Account;

import java.util.List;

import javax.annotation.Nullable;

class PopupAutoCompleteAccount extends PopupAutoComplete<Account> {
    public PopupAutoCompleteAccount(@android.support.annotation.Nullable List<Account> items, @NonNull OnPopupAutoCompleteListener<Account> onPopupAutoCompleteListener, @NonNull View anchorView) {
        super(items, onPopupAutoCompleteListener, anchorView);
    }

    @Nullable @Override public String apply(Account input) {
        return input.getTitle();
    }
}
