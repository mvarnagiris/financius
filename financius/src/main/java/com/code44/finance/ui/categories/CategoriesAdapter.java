package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.data.model.Category;
import com.code44.finance.ui.common.BaseModelsAdapter;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class CategoriesAdapter extends BaseModelsAdapter implements StickyListHeadersAdapter {
    public CategoriesAdapter(Context context) {
        super(context);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_category, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Category category = Category.from(cursor);
        holder.colorImageView.setColorFilter(category.getColor());
        holder.titleTextView.setText(category.getTitle());
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent) {
        mCursor.moveToPosition(position);
        final HeaderViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.li_category_header, parent, false);
            holder = HeaderViewHolder.setAsTag(convertView);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        holder.titleTextView.setText(mContext.getString(Category.from(mCursor).getTransactionType() == TransactionType.Expense ? R.string.expenses : R.string.income));

        return convertView;
    }

    @Override public long getHeaderId(int position) {
        mCursor.moveToPosition(position);
        return Category.from(mCursor).getTransactionType().ordinal();
    }

    private static class ViewHolder {
        public ImageView colorImageView;
        public TextView titleTextView;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.colorImageView = (ImageView) view.findViewById(R.id.colorImageView);
            holder.titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            view.setTag(holder);

            return holder;
        }
    }

    private static class HeaderViewHolder {
        public TextView titleTextView;

        public static HeaderViewHolder setAsTag(View view) {
            final HeaderViewHolder holder = new HeaderViewHolder();
            holder.titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            view.setTag(holder);

            return holder;
        }
    }
}
