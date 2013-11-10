package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.code44.finance.R;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class BigTextCardView extends TitleCardView
{
    public BigTextCardView(Context context)
    {
        this(context, null);
    }

    public BigTextCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BigTextCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        title_TV.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        title_TV.setTextColor(getResources().getColor(R.color.text_primary));
        title_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_xxlarge));
    }

    public static class BigTextCardInfo extends TitleCardInfo
    {
        public BigTextCardInfo(long id)
        {
            super(id);
        }
    }
}
