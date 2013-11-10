package com.code44.finance.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.code44.finance.views.ColorView;

/**
 * Created by Mantas on 05/06/13.
 */
public class ColorsAdapter extends BaseAdapter
{
    private final Context context;
    private final int[] colors;

    public ColorsAdapter(Context context, int[] colors)
    {
        this.context = context.getApplicationContext();
        this.colors = colors;
    }

    @Override
    public int getCount()
    {
        return colors.length;
    }

    @Override
    public Object getItem(int position)
    {
        return colors[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        if (view == null)
            view = new ColorView(context);

        ((ColorView) view).bind(colors[position]);

        return view;
    }
}