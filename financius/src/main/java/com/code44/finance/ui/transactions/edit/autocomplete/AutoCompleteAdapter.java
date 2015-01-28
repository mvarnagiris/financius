package com.code44.finance.ui.transactions.edit.autocomplete;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.transactions.edit.presenters.TransactionEditData;
import com.code44.finance.utils.ThemeUtils;

import java.util.List;

public abstract class AutoCompleteAdapter<T> implements View.OnClickListener {
    private static final int MAX_CHILD_COUNT = 3;

    private final ViewGroup containerView;
    private final AutoCompleteAdapterListener listener;
    private final OnAutoCompleteItemClickListener<T> clickListener;

    protected AutoCompleteAdapter(ViewGroup containerView, AutoCompleteAdapterListener listener, OnAutoCompleteItemClickListener<T> clickListener) {
        this.containerView = containerView;
        this.listener = listener;
        this.clickListener = clickListener;
    }

    @Override public void onClick(View v) {
        //noinspection unchecked
        clickListener.onAutoCompleteItemClick((T) v.getTag());
        hide();
    }

    public boolean show(AutoCompleteAdapter<?> currentAdapter, TransactionEditData transactionEditData) {
        containerView.removeAllViews();
        final AutoCompleteResult autoCompleteResult = transactionEditData.getAutoCompleteResult();
        final boolean isSameAdapterVisible = currentAdapter != null && isSameAdapter(currentAdapter);
        final boolean isAutoCompleteResultEmpty = autoCompleteResult == null || getItems(autoCompleteResult) == null || getItems(autoCompleteResult).isEmpty();
        if (isSameAdapterVisible || isAutoCompleteResultEmpty) {
            hide();
            return false;
        }

        if (currentAdapter != null) {
            currentAdapter.hide();
        }

        for (T item : getItems(autoCompleteResult)) {
            if (containerView.getChildCount() == MAX_CHILD_COUNT) {
                break;
            }

            if (!showItem(transactionEditData, item)) {
                continue;
            }

            final View view = newView(containerView.getContext(), containerView);
            bindView(view, item);
            containerView.addView(view);
            view.setBackground(ThemeUtils.getDrawable(containerView.getContext(), R.attr.selectableItemBackground));
            view.setTag(item);
            view.setOnClickListener(this);
        }

        containerView.setVisibility(View.VISIBLE);
        listener.onAutoCompleteAdapterShown(this);
        return true;
    }

    public void hide() {
        containerView.setVisibility(View.GONE);
        listener.onAutoCompleteAdapterHidden(this);
    }

    protected abstract View newView(Context context, ViewGroup containerView);

    protected abstract void bindView(View view, T item);

    protected abstract boolean isSameAdapter(AutoCompleteAdapter<?> currentAdapter);

    protected abstract boolean showItem(TransactionEditData transactionEditData, T item);

    protected abstract List<T> getItems(AutoCompleteResult autoCompleteResult);

    public static interface AutoCompleteAdapterListener {
        public void onAutoCompleteAdapterShown(AutoCompleteAdapter autoCompleteAdapter);

        public void onAutoCompleteAdapterHidden(AutoCompleteAdapter autoCompleteAdapter);
    }

    public static interface OnAutoCompleteItemClickListener<T> {
        public void onAutoCompleteItemClick(T item);
    }
}
