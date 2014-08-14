package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;

public class TagsAdapter extends BaseModelsAdapter {
    public TagsAdapter(Context context) {
        super(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_tag, parent, false);
        ViewHolder.setAsTag(view);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Tag tag = Tag.from(cursor);
        holder.title_TV.setText(tag.getTitle());
    }

    private static class ViewHolder {
        public TextView title_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
