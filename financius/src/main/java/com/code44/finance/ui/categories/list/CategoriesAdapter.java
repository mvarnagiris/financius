package com.code44.finance.ui.categories.list;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;
import com.code44.finance.ui.common.recycler.SectionsDecoration;

class CategoriesAdapter extends ModelsAdapter<Category> implements SectionsDecoration.Adapter<CategoriesAdapter.HeaderViewHolder> {
    public CategoriesAdapter(OnModelClickListener<Category> onModelClickListener) {
        super(onModelClickListener);
    }

    @Override protected ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category, parent, false));
    }

    @Override protected Category modelFromCursor(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override public long getHeaderId(int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        return Category.from(cursor).getTransactionType().ordinal();
    }

    @Override public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category_header, parent, false));
    }

    @Override public void onBindHeaderViewHolder(HeaderViewHolder viewHolder, int position) {
        final Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        viewHolder.titleTextView.setText(Category.from(cursor).getTransactionType() == TransactionType.Expense ? R.string.expenses : R.string.incomes);
    }

    static class ViewHolder extends ModelViewHolder<Category> {
        private final ImageView colorImageView;
        private final TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            colorImageView = (ImageView) itemView.findViewById(R.id.colorImageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        @Override protected void bind(Category model, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            colorImageView.setColorFilter(model.getColor());
            titleTextView.setText(model.getTitle());
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }
}
