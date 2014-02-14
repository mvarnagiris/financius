package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.db.Tables;

public class CategoryParentsAdapter extends AbstractCursorAdapter
{
    private final Typeface lightTypeface;
    private final Typeface regularTypeface;
    private int iTitle;
    private int iColor;
    private long selectedId;

    public CategoryParentsAdapter(Context context)
    {
        super(context, null);
        lightTypeface = Typeface.create("sans-serif-light", Typeface.NORMAL);
        regularTypeface = Typeface.DEFAULT;
        selectedId = -1;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_category, viewGroup, false);
        final ViewHolder holder = new ViewHolder();
        //noinspection ConstantConditions
        holder.color_IV = (ImageView) view.findViewById(R.id.color_IV);
        holder.color_IV.setVisibility(View.VISIBLE);
        holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();

        // Set values
        if (cursor.getPosition() == 0)
        {
            holder.color_IV.setVisibility(View.GONE);
            holder.title_TV.setText(R.string.create_new_main_category);
            holder.title_TV.setTypeface(regularTypeface);
        }
        else
        {
            holder.color_IV.setVisibility(View.VISIBLE);
            //noinspection ConstantConditions
            ((GradientDrawable) holder.color_IV.getDrawable()).setColor(cursor.getInt(iColor));
            holder.title_TV.setText(cursor.getString(iTitle));
            holder.title_TV.setTypeface(lightTypeface);
        }
    }

    public long getSelectedId()
    {
        return selectedId;
    }

    public void setSelectedId(long selectedId)
    {
        this.selectedId = selectedId;
    }

    @Override
    protected void findIndexes(Cursor c)
    {
        iTitle = c.getColumnIndex(Tables.Categories.TITLE);
        iColor = c.getColumnIndex(Tables.Categories.COLOR);
    }

    private static class ViewHolder
    {
        public ImageView color_IV;
        public TextView title_TV;
    }
}
