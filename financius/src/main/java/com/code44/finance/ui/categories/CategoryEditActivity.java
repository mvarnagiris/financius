package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.ui.ModelEditActivityOld;
import com.code44.finance.ui.ModelFragment;

public class CategoryEditActivity extends ModelEditActivityOld {
    private static final String EXTRA_CATEGORY_TYPE = "EXTRA_CATEGORY_TYPE";

    private TransactionType transactionType;

    public static void start(Context context, String categoryServerId, TransactionType transactionType) {
        final Intent intent = makeIntent(context, CategoryEditActivity.class, categoryServerId);
        intent.putExtra(EXTRA_CATEGORY_TYPE, transactionType);
        startActivity(context, intent);
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.category;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return CategoryEditFragment.newInstance(modelServerId, transactionType);
    }

    @Override protected void readExtras() {
        super.readExtras();
        transactionType = (TransactionType) getIntent().getSerializableExtra(EXTRA_CATEGORY_TYPE);
    }
}