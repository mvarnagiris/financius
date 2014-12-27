package com.code44.finance.ui.transactions.autocomplete.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;

import java.util.List;

public class AutoCompleteCategoriesAdapter extends AutoCompleteAdapter<Category> {
    public AutoCompleteCategoriesAdapter(ViewGroup containerView, AutoCompleteAdapterListener listener, OnAutoCompleteItemClickListener<Category> clickListener) {
        super(containerView, listener, clickListener);
    }

    @Override protected View newView(Context context, ViewGroup containerView) {
        return LayoutInflater.from(context).inflate(R.layout.li_category, containerView, false);
    }

    @Override protected void bindView(View view, Category category) {
        ((ImageView) view.findViewById(R.id.colorImageView)).setColorFilter(category.getColor());
        ((TextView) view.findViewById(R.id.titleTextView)).setText(category.getTitle());
    }

    @Override protected boolean isSameAdapter(AutoCompleteAdapter<?> currentAdapter) {
        return currentAdapter instanceof AutoCompleteCategoriesAdapter;
    }

    @Override protected List<Category> getItems(AutoCompleteResult autoCompleteResult) {
        return autoCompleteResult.getCategories();
    }
}
