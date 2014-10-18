package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.ModelEditFragment;
import com.code44.finance.ui.SelectColorFragment;

public class CategoryEditFragment extends ModelEditFragment<Category> implements View.OnClickListener, SelectColorFragment.OnColorSelectedListener {
    private static final String ARG_CATEGORY_TYPE = "ARG_CATEGORY_TYPE";

    private static final String FRAGMENT_SELECT_COLOR = CategoryEditFragment.class.getName() + ".FRAGMENT_SELECT_COLOR";

    private EditText title_ET;
    private ImageButton color_IB;

    private TransactionType transactionType;

    public static CategoryEditFragment newInstance(String categoryServerId, TransactionType transactionType) {
        final Bundle args = makeArgs(categoryServerId);
        args.putSerializable(ARG_CATEGORY_TYPE, transactionType);

        final CategoryEditFragment fragment = new CategoryEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transactionType = (TransactionType) getArguments().getSerializable(ARG_CATEGORY_TYPE);
        if (transactionType == null) {
            transactionType = TransactionType.Expense;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_edit, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        title_ET = (EditText) view.findViewById(R.id.title_ET);
        color_IB = (ImageButton) view.findViewById(R.id.color_IB);

        // Setup
        color_IB.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SelectColorFragment.setListenerIfVisible(getChildFragmentManager(), FRAGMENT_SELECT_COLOR, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SelectColorFragment.removeListenerIfVisible(getChildFragmentManager(), FRAGMENT_SELECT_COLOR);
    }

    @Override
    public boolean onSave(Context context, Category model) {
        boolean canSave = true;

        if (TextUtils.isEmpty(model.getTitle())) {
            canSave = false;
            // TODO Show error
        }

        if (canSave) {
            if (StringUtils.isEmpty(model.getId())) {
                model.setTransactionType(transactionType);
            }
            DataStore.insert().model(model).into(context, CategoriesProvider.uriCategories());
        }

        return canSave;
    }

    @Override
    protected void ensureModelUpdated(Category model) {
        model.setTitle(title_ET.getText().toString());
    }

    @Override
    protected CursorLoader getModelCursorLoader(Context context, String modelServerId) {
        return Tables.Categories.getQuery(null).asCursorLoader(context, CategoriesProvider.uriCategory(modelServerId));
    }

    @Override
    protected Category getModelFrom(Cursor cursor) {
        final Category category = Category.from(cursor);
        if (StringUtils.isEmpty(category.getId())) {
            category.setColor(0xff607d8b);
        }
        return category;
    }

    @Override
    protected void onModelLoaded(Category model) {
        title_ET.setText(model.getTitle());
        color_IB.setColorFilter(model.getColor());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_IB:
                SelectColorFragment.show(getChildFragmentManager(), FRAGMENT_SELECT_COLOR, model.getColor(), this);
                break;
        }
    }

    @Override
    public void onColorSelected(int color) {
        model.setColor(color);
        color_IB.setColorFilter(color);
    }
}
