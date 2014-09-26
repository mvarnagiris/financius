package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;

public class TagsAdapter extends BaseModelsAdapter {
    private final boolean isMultiChoice;

    public TagsAdapter(Context context, boolean isMultiChoice) {
        super(context);
        this.isMultiChoice = isMultiChoice;
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_tag, parent, false);
        final ViewHolder holder = ViewHolder.setAsTag(view);
        if (isMultiChoice) {
            holder.select_CB.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final Tag tag = Tag.from(cursor);
        holder.title_TV.setText(tag.getTitle());
        if (isMultiChoice) {
            holder.select_CB.setChecked(isModelSelected(tag));
        }
    }

    private static class ViewHolder {
        public CheckBox select_CB;
        public TextView title_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.select_CB = (CheckBox) view.findViewById(R.id.select_CB);
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
