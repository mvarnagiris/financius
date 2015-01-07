package com.code44.finance.ui.categories.list;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.categories.CategoryActivity;
import com.code44.finance.ui.categories.CategoryEditActivity;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.common.recycler.DividerDecoration;

class CategoriesActivityPresenter extends ModelsActivityPresenter<Category> {
    private static final String EXTRA_TRANSACTION_TYPE = CategoriesActivityPresenter.class.getName() + ".EXTRA_TRANSACTION_TYPE";

    private TransactionType transactionType;

    public static void addExtras(Intent intent, TransactionType transactionType) {
        intent.putExtra(EXTRA_TRANSACTION_TYPE, transactionType);
    }

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        transactionType = (TransactionType) activity.getIntent().getSerializableExtra(EXTRA_TRANSACTION_TYPE);
        super.onActivityCreated(activity, savedInstanceState);
    }

    @Override protected ModelsAdapter<Category> createAdapter(ModelsAdapter.OnModelClickListener<Category> defaultOnModelClickListener) {
        return new CategoriesAdapter(defaultOnModelClickListener);
    }

    @Override protected CursorLoader getModelsCursorLoader(Context context) {
        return Tables.Categories
                .getQuery(transactionType)
                .sortOrder(Tables.Categories.TRANSACTION_TYPE.getName())
                .sortOrder(Tables.Categories.SORT_ORDER.getName())
                .asCursorLoader(context, CategoriesProvider.uriCategories());
    }

    @Override protected void onModelClick(Context context, View view, Category model, Cursor cursor, int position) {
        CategoryActivity.start(context, model.getId());
    }

    @Override protected void startModelEdit(Context context, String modelId) {
        CategoryEditActivity.start(context, modelId);
    }

    @Override protected RecyclerView.ItemDecoration[] getItemDecorations() {
        final Context context = getActivity();
        return new RecyclerView.ItemDecoration[]{new DividerDecoration(context).setPaddingLeft(context.getResources().getDimensionPixelSize(R.dimen.keyline_content))};
    }

    private static class SectionsDecoration extends RecyclerView.ItemDecoration {
        @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (hasHeader(view, parent)) {

            }
        }

        @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
        }

        private boolean hasHeader(View view, RecyclerView parent) {
            final int adapterPosition = parent.getChildPosition(view);
            if (adapterPosition == 0) {
                return true;
            }

            //noinspection unchecked
            final ModelsAdapter.ViewHolder<Category> viewHolder = (ModelsAdapter.ViewHolder<Category>) parent.getChildViewHolder(view);
            if (viewHolder.getModel().getTransactionType() == TransactionType.Expense) {
                return false;
            }

            final int previousPosition = parent.indexOfChild(view) - 1;
            //noinspection unchecked
            final ModelsAdapter.ViewHolder<Category> previousViewHolder = (ModelsAdapter.ViewHolder<Category>) parent.getChildViewHolder(parent.getChildAt(previousPosition));
            return previousViewHolder.getModel().getTransactionType() == TransactionType.Expense;
        }
    }
}
