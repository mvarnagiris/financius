package com.code44.finance.ui.categories.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.utils.analytics.Analytics;

public class CategoryEditActivity extends BaseActivity/* implements View.OnClickListener, SelectColorFragment.OnColorSelectedListener */ {
//    private static final String FRAGMENT_SELECT_COLOR = CategoryEditActivity.class.getName() + ".FRAGMENT_SELECT_COLOR";

    public static void start(Context context, String categoryId) {
        final Intent intent = makeIntentForActivity(context, CategoryEditActivity.class);
        CategoryEditActivityPresenter.addExtras(intent, categoryId);
        startActivity(context, intent);
    }

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.activity_category_edit);
    }

    @Override protected ActivityPresenter onCreateActivityPresenter() {
        return new CategoryEditActivityPresenter(getEventBus());
    }

//    @Override public void onResume() {
//        super.onResume();
//        SelectColorFragment.setListenerIfVisible(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, this);
//    }
//
//    @Override public void onPause() {
//        super.onPause();
//        SelectColorFragment.removeListenerIfVisible(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR);
//    }

//    @Override public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.colorButton:
//                ensureModelUpdated(model);
//                SelectColorFragment.show(getSupportFragmentManager(), FRAGMENT_SELECT_COLOR, model.getColor(), this);
//                break;
//            case R.id.transactionTypeButton:
//                ensureModelUpdated(model);
//                onTransactionTypeChange();
//                break;
//        }
//    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.CategoryEdit;
    }

//    private void onTransactionTypeChange() {
//        if (model.getTransactionType() == TransactionType.Expense) {
//            model.setTransactionType(TransactionType.Income);
//        } else {
//            model.setTransactionType(TransactionType.Expense);
//        }
//        onModelLoaded(model);
//    }
}