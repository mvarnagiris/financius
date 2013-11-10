package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import com.code44.finance.R;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions", "deprecation"})
public class CardView extends FrameLayout
{
    public CardView(Context context)
    {
        this(context, null);
    }

    public CardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup background
        setBackgroundResource(R.drawable.bg_card_normal_new);
        setForeground(getResources().getDrawable(R.drawable.card_selector));

        // Setup
        final int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        setPadding(padding, padding, padding, padding);
    }

    @Override
    public int getPaddingBottom()
    {
        return (int) (super.getPaddingBottom() + TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
    }

    public void setCardInfo(CardInfo cardInfo)
    {

    }

    public static class CardInfo
    {
        private long id;
        private int type;

        public CardInfo(long id)
        {
            this.id = id;
        }

        public long getId()
        {
            return id;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;

            if (o == null || !this.getClass().isAssignableFrom(o.getClass())) return false;

            CardInfo cardInfo = (CardInfo) o;

            if (id != cardInfo.id) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            return (int) (id ^ (id >>> 32));
        }
    }
}