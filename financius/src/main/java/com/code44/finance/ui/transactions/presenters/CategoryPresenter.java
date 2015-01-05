package com.code44.finance.ui.transactions.presenters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.Presenter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.adapters.AutoCompleteCategoriesAdapter;
import com.code44.finance.utils.ThemeUtils;

public class CategoryPresenter extends Presenter implements AutoCompletePresenter<Category>, AutoCompleteAdapter.AutoCompleteAdapterListener {
    private final ImageView colorImageView;
    private final View categoryContainerView;
    private final Button categoryButton;
    private final View categoryDividerView;
    private final ViewGroup categoriesAutoCompleteContainerView;
    private TransactionType transactionType;

    public CategoryPresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
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

    @Override public AutoCompleteAdapter<Category> showAutoComplete(AutoCompleteAdapter<?> currentAdapter, TransactionEditData transactionEditData, AutoCompleteAdapter.OnAutoCompleteItemClickListener<Category> clickListener, View view) {
        final AutoCompleteCategoriesAdapter adapter = new AutoCompleteCategoriesAdapter(categoriesAutoCompleteContainerView, this, clickListener);
        if (adapter.show(currentAdapter, transactionEditData)) {
            return adapter;
        }
        return null;
    }

    @Override public void onAutoCompleteAdapterShown(AutoCompleteAdapter autoCompleteAdapter) {
        categoryButton.setHint(R.string.show_all);
        categoryDividerView.setVisibility(View.GONE);
    }

    @Override public void onAutoCompleteAdapterHidden(AutoCompleteAdapter autoCompleteAdapter) {
        categoryButton.setHint(R.string.categories_one);
        categoryDividerView.setVisibility(View.VISIBLE);
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
    }

    public void setCategory(Category category) {
        colorImageView.setColorFilter(getCategoryColor(category, transactionType));
        categoryButton.setText(category == null ? null : category.getTitle());
    }

    private int getCategoryColor(Category category, TransactionType transactionType) {
        if (category == null) {
            switch (transactionType) {
                case Expense:
                    return ThemeUtils.getColor(categoryButton.getContext(), R.attr.textColorNegative);
                case Income:
                    return ThemeUtils.getColor(categoryButton.getContext(), R.attr.textColorPositive);
                case Transfer:
                    return ThemeUtils.getColor(categoryButton.getContext(), R.attr.textColorNeutral);
                default:
                    throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
            }
        } else {
            return category.getColor();
        }
    }
}
