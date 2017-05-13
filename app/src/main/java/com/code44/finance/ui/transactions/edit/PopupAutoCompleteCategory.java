package com.code44.finance.ui.transactions.edit;

import android.support.annotation.NonNull;
import android.view.View;

import com.code44.finance.data.model.Category;

import java.util.List;

import javax.annotation.Nullable;

class PopupAutoCompleteCategory extends PopupAutoComplete<Category> {
    public PopupAutoCompleteCategory(@android.support.annotation.Nullable List<Category> items, @NonNull OnPopupAutoCompleteListener<Category> onPopupAutoCompleteListener, @NonNull View anchorView) {
        super(items, onPopupAutoCompleteListener, anchorView);
    }

    @Nullable @Override public String apply(Category input) {
        return input.getTitle();
    }
}
