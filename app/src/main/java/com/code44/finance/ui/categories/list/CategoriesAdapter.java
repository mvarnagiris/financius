package com.code44.finance.ui.categories.list;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelViewHolder;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.recycler.SectionsDecoration;

class CategoriesAdapter extends ModelsAdapter<Category, CategoriesAdapter.ViewHolder> implements SectionsDecoration.Adapter<CategoriesAdapter.HeaderViewHolder> {
    public CategoriesAdapter(@NonNull OnModelClickListener<Category> onModelClickListener, @NonNull ModelsActivity.Mode mode) {
        super(onModelClickListener, mode);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_category, parent, false), this);
    }

    @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Category model, boolean isSelected) {
        holder.colorImageView.setColorFilter(model.getColor());
        holder.titleTextView.setText(model.getTitle());
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
        final TransactionType transactionType = Category.from(cursor).getTransactionType();
        viewHolder.titleTextView.setText(transactionType == TransactionType.Expense ? R.string.expenses : R.string.incomes);
    }

    static class ViewHolder extends ModelViewHolder {
        private final ImageView colorImageView;
        private final TextView titleTextView;

        public ViewHolder(@NonNull View itemView, @NonNull OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            colorImageView = (ImageView) itemView.findViewById(R.id.colorImageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }
}
