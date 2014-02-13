package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.mobeta.android.dslv.DragSortListView;

@SuppressWarnings("ConstantConditions")
public class CategoriesAdapter extends AbstractCursorAdapter implements DragSortListView.DropListener
{
    private static final int VT_HEADER = 0;
    private static final int VT_NORMAL = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private final int selectedColor;
    // -----------------------------------------------------------------------------------------------------------------
    private int iId;
    private int iTitle;
    private int iLevel;
    private int iColor;
    // -----------------------------------------------------------------------------------------------------------------
    private String query;
    private ForegroundColorSpan querySpan;
    private int categoryType;

    public CategoriesAdapter(Context context)
    {
        super(context, null);
        querySpan = new ForegroundColorSpan(context.getResources().getColor(R.color.text_accent));
        selectedColor = context.getResources().getColor(R.color.f_brand_lighter2);
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        mCursor.moveToPosition(position);
        return mCursor.getInt(iLevel) == 2 ? VT_NORMAL : VT_HEADER;
    }

    @Override
    public View newView(Context context, Cursor c, ViewGroup root)
    {
        final int viewType = getItemViewType(c.getPosition());

        final View view = LayoutInflater.from(context).inflate(viewType == VT_NORMAL ? R.layout.li_category : R.layout.li_category_header, root, false);
        final ViewHolder holder = new ViewHolder();
        holder.color_IV = (ImageView) view.findViewById(R.id.color_IV);
        holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor c)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();
        final int viewType = getItemViewType(c.getPosition());

        // Set values
        final String title = c.getString(iTitle);
        final int index = TextUtils.isEmpty(query) ? -1 : title.toLowerCase().indexOf(query.toLowerCase());
        if (index == -1)
        {
            holder.title_TV.setText(title);
        }
        else
        {
            final SpannableStringBuilder ssb = new SpannableStringBuilder(title);
            ssb.setSpan(querySpan, index, index + query.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title_TV.setText(ssb);
        }

        if (viewType == VT_HEADER || index >= 0)
        {
            holder.color_IV.setVisibility(View.VISIBLE);
            GradientDrawable drawable = (GradientDrawable) holder.color_IV.getDrawable();
            drawable.setColor(c.getInt(iColor));
        }
        else
        {
            holder.color_IV.setVisibility(View.INVISIBLE);
        }

        view.setBackgroundColor(isSelected(c.getLong(iId)) ? selectedColor : 0);
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public void setCategoryType(int categoryType)
    {
        this.categoryType = categoryType;
    }

    public int getTopSectionStart(int position)
    {
        mCursor.moveToPosition(position);
        if (mCursor.getInt(iLevel) == 2)
        {
            do
            {
                if (mCursor.getInt(iLevel) == 1)
                    return mCursor.getPosition();
            }
            while (mCursor.moveToPrevious());
        }

        return -1;
    }

    public int getBottomSectionStart(int position)
    {
        mCursor.moveToPosition(position);
        if (mCursor.getInt(iLevel) == 2)
        {
            do
            {
                if (mCursor.getInt(iLevel) == 1)
                    return mCursor.getPosition();
            }
            while (mCursor.moveToNext());
        }

        return mCursor.getCount();
    }

    @Override
    public void drop(int from, int to)
    {
        if (from != to)
            API.swapCategories(mContext, from, to, categoryType);
    }

    @Override
    protected void findIndexes(Cursor c)
    {
        iId = c.getColumnIndex(Tables.Categories.ID);
        iTitle = c.getColumnIndex(Tables.Categories.TITLE);
        iLevel = c.getColumnIndex(Tables.Categories.LEVEL);
        iColor = c.getColumnIndex(Tables.Categories.COLOR);
    }

    private static class ViewHolder
    {
        public ImageView color_IV;
        public TextView title_TV;
    }
}