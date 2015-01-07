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

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ModelEditActivityPresenter;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.ThemeUtils;

class CategoryEditActivityPresenter extends ModelEditActivityPresenter<Category> implements TextWatcher, View.OnClickListener {
    private View transactionTypeContainerView;
    private ImageView transactionTypeImageView;
    private EditText titleEditText;
    private TransactionType transactionType;
    private Integer color;
    private String title;

    public CategoryEditActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);

        transactionTypeContainerView = findView(activity, R.id.transactionTypeContainerView);
        transactionTypeImageView = findView(activity, R.id.transactionTypeImageView);
        titleEditText = findView(activity, R.id.titleEditText);

        titleEditText.addTextChangedListener(this);
        transactionTypeContainerView.setOnClickListener(this);
        if (!isNewModel()) {
            transactionTypeContainerView.setVisibility(View.GONE);
        }
    }

    @Override protected void onDataChanged(Category storedModel) {
        titleEditText.setText(getTitle());
        titleEditText.setSelection(titleEditText.getText().length());

        final int color;
        final TransactionType transactionType = getTransactionType();
        switch (transactionType) {
            case Expense:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorNegative);
                break;
            case Income:
                color = ThemeUtils.getColor(transactionTypeImageView.getContext(), R.attr.textColorPositive);
                break;
            default:
                throw new IllegalArgumentException("Transaction type " + transactionType + " is not supported.");
        }
        transactionTypeImageView.setColorFilter(color);
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
            case R.id.colorButton:
//                ensureModelUpdated(model);
//                SelectColorFragment.show(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, model.getColor(), this);
                break;
            case R.id.transactionTypeContainerView:
                toggleTransactionType();
                break;
        }
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

        return ThemeUtils.getColor(titleEditText.getContext(), getTransactionType() == TransactionType.Expense ? R.attr.textColorNegative : R.attr.textColorNegative);
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
