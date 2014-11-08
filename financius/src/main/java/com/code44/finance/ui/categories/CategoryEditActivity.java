package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.SelectColorFragment;
import com.code44.finance.ui.common.ModelEditActivity;
import com.code44.finance.utils.analytics.Analytics;

public class CategoryEditActivity extends ModelEditActivity<Category> implements View.OnClickListener, SelectColorFragment.OnColorSelectedListener {
    private static final String FRAGMENT_SELECT_COLOR = CategoryEditActivity.class.getName() + ".FRAGMENT_SELECT_COLOR";

    private EditText titleEditText;
    private View containerView;
    private Button transactionTypeButton;

    public static void start(Context context, String categoryId) {
        final Intent intent = makeIntent(context, CategoryEditActivity.class, categoryId);
        startActivity(context, intent);
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_category_edit;
    }

    @Override protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        // Get views
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        containerView = findViewById(R.id.containerView);
        transactionTypeButton = (Button) findViewById(R.id.transactionTypeButton);
        final Button colorButton = (Button) findViewById(R.id.colorButton);

        // Setup
        transactionTypeButton.setOnClickListener(this);
        colorButton.setOnClickListener(this);
        transactionTypeButton.setEnabled(isNewModel());
    }

    @Override public void onResume() {
        super.onResume();
        SelectColorFragment.setListenerIfVisible(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, this);
    }

    @Override public void onPause() {
        super.onPause();
        SelectColorFragment.removeListenerIfVisible(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR);
    }

    @Override protected boolean onSave(Category model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getTitle())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            DataStore.insert().model(model).into(this, CategoriesProvider.uriCategories());
        }

        return canSave;
    }

    @Override protected void ensureModelUpdated(Category model) {
        model.setTitle(titleEditText.getText().toString());
    }

    @Override protected CursorLoader getModelCursorLoader(String modelId) {
        return Tables.Categories.getQuery(null).asCursorLoader(this, CategoriesProvider.uriCategory(modelId));
    }

    @Override protected Category getModelFrom(Cursor cursor) {
        final Category category = Category.from(cursor);
        if (StringUtils.isEmpty(category.getId())) {
            category.setColor(0xff607d8b);
        }
        return category;
    }

    @Override protected void onModelLoaded(Category model) {
        titleEditText.setText(model.getTitle());
        containerView.setBackgroundColor(model.getColor());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(model.getColor());
            getWindow().setNavigationBarColor(model.getColor());
        }

        transactionTypeButton.setText(model.getTransactionType() == TransactionType.Expense ? R.string.expense : R.string.income);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.colorButton:
                ensureModelUpdated(model);
                SelectColorFragment.show(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, model.getColor(), this);
                break;
            case R.id.transactionTypeButton:
                ensureModelUpdated(model);
                onTransactionTypeChange();
                break;
        }
    }

    @Override public void onColorSelected(int color) {
        model.setColor(color);
        onModelLoaded(model);
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoryEdit;
    }

    private void onTransactionTypeChange() {
        if (model.getTransactionType() == TransactionType.Expense) {
            model.setTransactionType(TransactionType.Income);
        } else {
            model.setTransactionType(TransactionType.Expense);
        }
        onModelLoaded(model);
    }
}