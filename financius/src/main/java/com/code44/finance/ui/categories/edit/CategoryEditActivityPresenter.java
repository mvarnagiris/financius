package com.code44.finance.ui.categories.edit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.SelectColorFragment;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.ThemeUtils;

class CategoryEditActivityPresenter extends ModelEditActivityPresenter<Category> implements TextWatcher, View.OnClickListener, SelectColorFragment.OnColorSelectedListener {
    private static final String FRAGMENT_SELECT_COLOR = CategoryEditActivityPresenter.class.getName() + ".FRAGMENT_SELECT_COLOR";

    private ImageView colorImageView;
    private ImageView transactionTypeImageView;
    private TextView transactionTypeTextView;
    private EditText titleEditText;

    private TransactionType transactionType;
    private Integer color;
    private String title;

    public CategoryEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        final View colorContainerView = findView(activity, R.id.colorContainerView);
        final View transactionTypeContainerView = findView(activity, R.id.transactionTypeContainerView);
        final View transactionTypeDividerView = findView(activity, R.id.transactionTypeDividerView);
        colorImageView = findView(activity, R.id.colorImageView);
        transactionTypeImageView = findView(activity, R.id.transactionTypeImageView);
        transactionTypeTextView = findView(activity, R.id.transactionTypeTextView);
        titleEditText = findView(activity, R.id.titleEditText);

        titleEditText.addTextChangedListener(this);
        colorContainerView.setOnClickListener(this);
        transactionTypeContainerView.setOnClickListener(this);
        if (!isNewModel()) {
            transactionTypeContainerView.setVisibility(View.GONE);
            transactionTypeDividerView.setVisibility(View.GONE);
        }
    }

    @Override public void onActivityResumed(BaseActivity activity) {
        super.onActivityResumed(activity);
        SelectColorFragment.setListenerIfVisible(activity.getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, this);
    }

    @Override public void onActivityPaused(BaseActivity activity) {
        super.onActivityPaused(activity);
        SelectColorFragment.removeListenerIfVisible(activity.getSupportFragmentManager(), FRAGMENT_SELECT_COLOR);
    }

    @Override protected void onDataChanged(Category storedModel) {
        titleEditText.setText(getTitle());
        titleEditText.setSelection(titleEditText.getText().length());
        colorImageView.setColorFilter(getColor());

        final int color;
        final String transactionTypeTitle;
        final TransactionType transactionType = getTransactionType();
        switch (transactionType) {
            case Expense:
                color = ThemeUtils.getColor(transactionTypeTextView.getContext(), R.attr.textColorNegative);
                transactionTypeTitle = transactionTypeTextView.getContext().getString(R.string.expense);
                break;
            case Income:
                color = ThemeUtils.getColor(transactionTypeTextView.getContext(), R.attr.textColorPositive);
                transactionTypeTitle = transactionTypeTextView.getContext().getString(R.string.income);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }
        transactionTypeImageView.setColorFilter(color);
        transactionTypeTextView.setText(transactionTypeTitle);
    }

    @Override protected boolean onSave() {
        boolean canSave = true;

        final String title = getTitle();
        if (TextUtils.isEmpty(title)) {
            canSave = false;
            titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.textColorNegative));
        }

        if (canSave) {
            final Category category = new Category();
            category.setId(getId());
            category.setTransactionType(getTransactionType());
            category.setColor(getColor());
            category.setTitle(title);

            DataStore.insert().model(category).into(titleEditText.getContext(), CategoriesProvider.uriCategories());
        }

        return canSave;
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(context, CategoriesProvider.uriCategory(modelId));
    }

    @Override protected Category getModelFrom(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
        title = titleEditText.getText().toString();
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.colorContainerView:
                SelectColorFragment.show(getActivity().getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, getColor(), this);
                break;
            case R.id.transactionTypeContainerView:
                toggleTransactionType();
                break;
        }
    }

    @Override public void onColorSelected(int color) {
        this.color = color;
        onDataChanged(getStoredModel());
    }

    private String getId() {
        return getStoredModel() != null ? getStoredModel().getId() : null;
    }

    private TransactionType getTransactionType() {
        if (transactionType != null) {
            return transactionType;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTransactionType();
        }

        return TransactionType.Expense;
    }

    private int getColor() {
        if (color != null) {
            return color;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getColor();
        }

        return ThemeUtils.getColor(titleEditText.getContext(), getTransactionType() == TransactionType.Expense ? R.attr.textColorNegative : R.attr.textColorPositive);
    }

    private String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }

    private void toggleTransactionType() {
        switch (getTransactionType()) {
            case Expense:
                transactionType = TransactionType.Income;
                break;
            case Income:
                transactionType = TransactionType.Expense;
                break;
            default:
                throw new IllegalArgumentException("TransactionType " + transactionType + " is not supported.");
        }

        onDataChanged(getStoredModel());
    }
}
