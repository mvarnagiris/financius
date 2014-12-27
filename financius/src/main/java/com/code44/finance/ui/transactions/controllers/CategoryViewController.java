package com.code44.finance.ui.transactions.controllers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.adapters.AutoCompleteCategoriesAdapter;

public class CategoryViewController extends ViewController implements AutoCompleteController<Category>, AutoCompleteAdapter.AutoCompleteAdapterListener {
    private final ImageView colorImageView;
    private final View categoryContainerView;
    private final Button categoryButton;
    private final View categoryDividerView;
    private final ViewGroup categoriesAutoCompleteContainerView;
    private TransactionType transactionType;

    public CategoryViewController(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        colorImageView = findView(activity, R.id.colorImageView);
        categoryContainerView = findView(activity, R.id.categoryContainerView);
        categoryButton = findView(activity, R.id.categoryButton);
        categoryDividerView = findView(activity, R.id.categoryDividerView);
        categoriesAutoCompleteContainerView = findView(activity, R.id.categoriesAutoCompleteContainerView);

        categoryButton.setOnClickListener(clickListener);
        categoryButton.setOnLongClickListener(longClickListener);
    }

    @Override public void showError(Throwable error) {
    }

    @Override public AutoCompleteAdapter<Category> show(AutoCompleteAdapter<?> currentAdapter, AutoCompleteResult autoCompleteResult, AutoCompleteAdapter.OnAutoCompleteItemClickListener<Category> clickListener) {
        final AutoCompleteCategoriesAdapter adapter = new AutoCompleteCategoriesAdapter(categoriesAutoCompleteContainerView, this, clickListener);
        if (adapter.show(currentAdapter, autoCompleteResult)) {
            return adapter;
        }
        return null;
    }

    @Override public void onAutoCompleteAdapterShown() {
        categoryButton.setHint(R.string.show_all);
    }

    @Override public void onAutoCompleteAdapterHidden() {
        categoryButton.setHint(R.string.categories_one);
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        switch (transactionType) {
            case Expense:
                colorImageView.setVisibility(View.VISIBLE);
                categoryContainerView.setVisibility(View.VISIBLE);
                categoryDividerView.setVisibility(View.VISIBLE);
                break;
            case Income:
                colorImageView.setVisibility(View.VISIBLE);
                categoryContainerView.setVisibility(View.VISIBLE);
                categoryDividerView.setVisibility(View.VISIBLE);
                break;
            case Transfer:
                colorImageView.setVisibility(View.GONE);
                categoryContainerView.setVisibility(View.GONE);
                categoryDividerView.setVisibility(View.GONE);
                break;
        }
        categoriesAutoCompleteContainerView.setVisibility(View.GONE);
    }

    public void setCategory(Category category) {
        colorImageView.setColorFilter(getCategoryColor(category, transactionType));
        categoryButton.setText(category == null ? null : category.getTitle());
    }

    public void setIsSetByUser(boolean isSetByUser) {
//        colorImageView.setImageAlpha(isSetByUser ? 255 : 64);
    }

    private int getCategoryColor(Category category, TransactionType transactionType) {
        if (category == null) {
            switch (transactionType) {
                case Expense:
                    return categoryButton.getResources().getColor(R.color.text_negative);
                case Income:
                    return categoryButton.getResources().getColor(R.color.text_positive);
                case Transfer:
                    return categoryButton.getResources().getColor(R.color.text_neutral);
                default:
                    throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
            }
        } else {
            return category.getColor();
        }
    }
}
