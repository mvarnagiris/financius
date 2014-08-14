package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.CategoryOwner;
import com.code44.finance.data.model.Category;

public class CategoriesAdapter extends BaseModelsAdapter {
    public CategoriesAdapter(Context context) {
        super(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_category, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Category category = Category.from(cursor);
        holder.color_IV.setColorFilter(category.getColor());
        if (category.getCategoryOwner() == CategoryOwner.SYSTEM) {
            holder.title_TV.setText(context.getString(R.string.no_category));
            holder.title_TV.setTextColor(context.getResources().getColor(R.color.text_secondary));
        } else {
            holder.title_TV.setText(category.getTitle());
            holder.title_TV.setTextColor(context.getResources().getColor(R.color.text_primary));
        }
    }

    private static class ViewHolder {
        public ImageView color_IV;
        public TextView title_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.color_IV = (ImageView) view.findViewById(R.id.color_IV);
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
