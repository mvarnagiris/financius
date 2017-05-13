package com.code44.finance.ui.transactions.edit;

import android.support.annotation.NonNull;
import android.view.View;

import com.code44.finance.data.model.Tag;

import java.util.List;

import javax.annotation.Nullable;

class PopupAutoCompleteTags extends PopupAutoComplete<List<Tag>> {
    public PopupAutoCompleteTags(@android.support.annotation.Nullable List<List<Tag>> items, @NonNull OnPopupAutoCompleteListener<List<Tag>> onPopupAutoCompleteListener, @NonNull View anchorView) {
        super(items, onPopupAutoCompleteListener, anchorView);
    }

    @Nullable @Override public String apply(List<Tag> input) {
        final StringBuilder sb = new StringBuilder();
        for (Tag tag : input) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(tag.getTitle());
        }
        return sb.toString();
    }
}
