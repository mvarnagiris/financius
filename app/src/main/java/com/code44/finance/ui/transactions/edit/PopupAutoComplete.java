package com.code44.finance.ui.transactions.edit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;

import com.code44.finance.R;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

abstract class PopupAutoComplete<T> implements AdapterView.OnItemClickListener, Function<T, String> {
    private List<T> items;
    private OnPopupAutoCompleteListener<T> onPopupAutoCompleteListener;
    private ListPopupWindow autoCompleteListPopupWindow;
    private View anchorView;

    protected PopupAutoComplete(@Nullable List<T> items, @NonNull OnPopupAutoCompleteListener<T> onPopupAutoCompleteListener, @NonNull View anchorView) {
        this.items = items == null || items.isEmpty() ? Collections.<T>emptyList() : items.subList(1, items.size());
        this.onPopupAutoCompleteListener = checkNotNull(onPopupAutoCompleteListener, "OnPopupAutoCompleteListener cannot be null.");
        this.anchorView = checkNotNull(anchorView, "Anchor View cannot be null.");
    }

    public void show() {
        checkState(anchorView != null, "This popup has already been dismissed. You need to create a new one.");

        if (autoCompleteListPopupWindow != null) {
            autoCompleteListPopupWindow.dismiss();
        }

        final List<String> items = new ArrayList<>(Collections2.transform(this.items, this));
        items.add(anchorView.getContext().getString(R.string.show_all));

        autoCompleteListPopupWindow = new ListPopupWindow(anchorView.getContext());
        autoCompleteListPopupWindow.setAdapter(new ArrayAdapter<>(anchorView.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items));
        autoCompleteListPopupWindow.setAnchorView(anchorView);
        autoCompleteListPopupWindow.setWidth(anchorView.getWidth());
        autoCompleteListPopupWindow.setOnItemClickListener(this);
        autoCompleteListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override public void onDismiss() {
                PopupAutoComplete.this.items = null;
                autoCompleteListPopupWindow = null;
                onPopupAutoCompleteListener = null;
                anchorView = null;
            }
        });
        autoCompleteListPopupWindow.show();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == parent.getAdapter().getCount() - 1) {
            onPopupAutoCompleteListener.onAutoCompleteShowAll();
        } else {
            onPopupAutoCompleteListener.onAutoCompleteSelected(items.get(position));
        }

        if (autoCompleteListPopupWindow != null) {
            autoCompleteListPopupWindow.dismiss();
        }
    }

    public interface OnPopupAutoCompleteListener<T> {
        void onAutoCompleteSelected(T selectedItem);

        void onAutoCompleteShowAll();
    }
}
