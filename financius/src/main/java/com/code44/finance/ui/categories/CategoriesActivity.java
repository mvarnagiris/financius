package com.code44.finance.ui.categories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.view.Menu;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.common.BaseModelsAdapter;
import com.code44.finance.ui.common.ModelListActivity;

import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

public class CategoriesActivity extends ModelListActivity {
    private static final String EXTRA_TRANSACTION_TYPE = "EXTRA_TRANSACTION_TYPE";

    private TransactionType transactionType;

    public static void start(Context context) {
        final Intent intent = makeViewIntent(context, CategoriesActivity.class);
        startActivity(context, intent);
    }

    public static void startSelect(Activity activity, int requestCode, TransactionType transactionType) {
        final Intent intent = makeSelectIntent(activity, CategoriesActivity.class);
        intent.putExtra(EXTRA_TRANSACTION_TYPE, transactionType);
        startActivityForResult(activity, intent, requestCode);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override protected int getLayoutId() {
        return R.layout.activity_categories;
    }

    @Override protected BaseModelsAdapter createAdapter() {
        return new CategoriesAdapter(this);
    }

    @Override protected CursorLoader getModelsCursorLoader() {
        return Tables.Categories
                .getQuery(transactionType)
                .sortOrder(Tables.Categories.TRANSACTION_TYPE.getName())
                .sortOrder(Tables.Categories.SORT_ORDER.getName())
                .asCursorLoader(this, CategoriesProvider.uriCategories());
    }

    @Override protected Model modelFrom(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override protected void onModelClick(View view, int position, String modelId, Model model) {
        CategoryActivity.start(this, modelId);
    }

    @Override protected void startModelEdit(String modelId) {
        CategoryEditActivity.start(this, modelId);
    }

    @Override protected void onExtras(Intent extras) {
        super.onExtras(extras);
        transactionType = (TransactionType) extras.getSerializableExtra(EXTRA_TRANSACTION_TYPE);
    }

    @Override protected void onSetupList(BaseModelsAdapter adapter) {
        @SuppressLint("WrongViewCast") final ExpandableStickyListHeadersListView listView = (ExpandableStickyListHeadersListView) findViewById(R.id.listView);
        listView.setAdapter((CategoriesAdapter) adapter);
        listView.setOnItemClickListener(this);
    }
}
