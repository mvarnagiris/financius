package com.code44.finance.ui.categories.edit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.ui.common.fragments.SelectColorFragment;
import com.code44.finance.ui.dialogs.ColorDialogFragment;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.analytics.Screens;
import com.squareup.otto.Subscribe;

public class CategoryEditActivity extends ModelEditActivity<Category, CategoryEditData> implements TextWatcher, View.OnClickListener, SelectColorFragment.OnColorSelectedListener {
    private static final String FRAGMENT_SELECT_COLOR = CategoryEditActivity.class.getName() + ".FRAGMENT_SELECT_COLOR";
    private static final String FRAGMENT_SELECT_COLOR_DIALOG = CategoryEditActivity.class.getName() + ".FRAGMENT_SELECT_COLOR_DIALOG";

    private static final int REQUEST_COLOR = 1;

    private ImageView colorImageView;
    private ImageView transactionTypeImageView;
    private TextView transactionTypeTextView;
    private EditText titleEditText;

    public static void start(Context context, String categoryId) {
        makeActivityStarter(context, CategoryEditActivity.class, categoryId).start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_edit);

        // Get views
        final View colorContainerView = findViewById(R.id.colorContainerView);
        final View transactionTypeContainerView = findViewById(R.id.transactionTypeContainerView);
        final View transactionTypeDividerView = findViewById(R.id.transactionTypeDividerView);
        colorImageView = (ImageView) findViewById(R.id.colorImageView);
        transactionTypeImageView = (ImageView) findViewById(R.id.transactionTypeImageView);
        transactionTypeTextView = (TextView) findViewById(R.id.transactionTypeTextView);
        titleEditText = (EditText) findViewById(R.id.titleEditText);

        titleEditText.addTextChangedListener(this);
        colorContainerView.setOnClickListener(this);
        transactionTypeContainerView.setOnClickListener(this);
        if (!isNewModel()) {
            transactionTypeContainerView.setVisibility(View.GONE);
            transactionTypeDividerView.setVisibility(View.GONE);
        }
    }

    @Override protected void onResume() {
        super.onResume();

        SelectColorFragment.setListenerIfVisible(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, this);
        getEventBus().register(this);
    }

    @Override protected void onPause() {
        super.onPause();

        SelectColorFragment.removeListenerIfVisible(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR);
        getEventBus().unregister(this);
    }

    @NonNull @Override protected CursorLoader getModelCursorLoader(@NonNull String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(this, CategoriesProvider.uriCategory(modelId));
    }

    @NonNull @Override protected Category getModelFrom(@NonNull Cursor cursor) {
        return Category.from(cursor);
    }

    @NonNull @Override protected CategoryEditData createModelEditData() {
        return new CategoryEditData(this);
    }

    @NonNull @Override protected ModelEditValidator<CategoryEditData> createModelEditValidator() {
        return new CategoryEditValidator(titleEditText);
    }

    @Override protected void onDataChanged(@NonNull CategoryEditData modelEditData) {
        final CategoryEditData categoryEditData = getModelEditData();
        titleEditText.setText(categoryEditData.getTitle());
        titleEditText.setSelection(titleEditText.getText().length());
        colorImageView.setColorFilter(categoryEditData.getColor());

        final int color;
        final String transactionTypeTitle;
        final TransactionType transactionType = categoryEditData.getTransactionType();
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

    @NonNull @Override protected Uri getSaveUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.colorContainerView:
                ColorDialogFragment.build(REQUEST_COLOR)
                        .setNegativeButtonText(getString(R.string.cancel))
                        .build()
                        .show(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR_DIALOG);
                break;
            case R.id.transactionTypeContainerView:
                toggleTransactionType();
                break;
        }
    }

    @Override public void onColorSelected(int color) {
        getModelEditData().setColor(color);
        onDataChanged(getModelEditData());
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override public void afterTextChanged(Editable s) {
        getModelEditData().setTitle(titleEditText.getText().toString());
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.CategoryEdit;
    }

    @Subscribe public void onColorSelectedFromDialog(ListDialogFragment.ListDialogEvent event) {
        if (event.getRequestCode() != REQUEST_COLOR) {
            return;
        }

        if (event.getPosition() == 0) {
            event.dismiss();
            SelectColorFragment.show(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, getModelEditData().getColor(), this);
            return;
        }

        if (!event.isActionButtonClicked()) {
            onColorSelected(((ColorDialogFragment.ColorListDialogItem) event.getAdapter().getItem(event.getPosition())).getColor());
            event.dismiss();
        }
    }

    private void toggleTransactionType() {
        switch (getModelEditData().getTransactionType()) {
            case Expense:
                getModelEditData().setTransactionType(TransactionType.Income);
                break;
            case Income:
                getModelEditData().setTransactionType(TransactionType.Expense);
                break;
            default:
                throw new IllegalArgumentException("TransactionType " + getModelEditData().getTransactionType() + " is not supported.");
        }

        onDataChanged(getModelEditData());
    }
}