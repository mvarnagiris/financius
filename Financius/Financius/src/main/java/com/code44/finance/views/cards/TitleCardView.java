package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class TitleCardView extends CardView
{
    protected final TextView title_TV;
    protected final TextView secondaryTitle_TV;
    protected final TextView subTitle_TV;
    // -----------------------------------------------------------------------------------------------------------------


    public TitleCardView(Context context)
    {
        this(context, null);
    }

    public TitleCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TitleCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Init
        title_TV = new TextView(context);
        secondaryTitle_TV = new TextView(context);
        subTitle_TV = new TextView(context);

        // Setup
        if (!isInEditMode())
        {
            final Typeface titleTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
            title_TV.setTypeface(titleTypeface);
            secondaryTitle_TV.setTypeface(titleTypeface);
            subTitle_TV.setTypeface(titleTypeface);
        }

        title_TV.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        title_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
        title_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        title_TV.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.space_normal));
        title_TV.setVisibility(GONE);

        secondaryTitle_TV.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        secondaryTitle_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
        secondaryTitle_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        secondaryTitle_TV.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.space_normal));
        secondaryTitle_TV.setVisibility(GONE);

        final LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        subTitle_TV.setLayoutParams(params);
        subTitle_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        subTitle_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        subTitle_TV.setCompoundDrawablePadding(getResources().getDimensionPixelSize(R.dimen.space_normal));
        subTitle_TV.setVisibility(GONE);

        // Add views
        addView(title_TV);
        addView(secondaryTitle_TV);
        addView(subTitle_TV);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;

        final int widthMS = MeasureSpec.makeMeasureSpec(width - getPaddingLeft() - getPaddingRight(), MeasureSpec.AT_MOST);
        final int heightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        // Secondary title
        if (secondaryTitle_TV.getVisibility() != GONE)
            measureChildWithMargins(secondaryTitle_TV, widthMS, 0, heightMS, 0);

        // Title
        if (title_TV.getVisibility() != GONE)
            measureChildWithMargins(title_TV, widthMS, secondaryTitle_TV.getMeasuredWidth(), heightMS, 0);

        // Increase height
        height += Math.max(title_TV.getMeasuredHeight(), secondaryTitle_TV.getMeasuredHeight());

        // Sub title
        if (subTitle_TV.getVisibility() != GONE)
        {
            measureChildWithMargins(subTitle_TV, widthMS, 0, heightMS, 0);

            // Increase height
            height += subTitle_TV.getHeight() + ((LayoutParams) subTitle_TV.getLayoutParams()).topMargin;
        }

        // Account for padding
        height += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        // Title
        if (title_TV.getVisibility() != GONE)
        {
            final LayoutParams params = (LayoutParams) title_TV.getLayoutParams();

            final int childLeft = parentLeft + params.leftMargin;
            final int childTop = parentTop + params.topMargin;
            title_TV.layout(childLeft, childTop, childLeft + title_TV.getMeasuredWidth(), childTop + title_TV.getMeasuredHeight());
        }

        // Secondary title
        if (secondaryTitle_TV.getVisibility() != GONE)
        {
            final LayoutParams params = (LayoutParams) secondaryTitle_TV.getLayoutParams();

            final int childRight = parentRight - params.rightMargin;
            final int childTop = parentTop + params.topMargin;
            secondaryTitle_TV.layout(childRight - secondaryTitle_TV.getMeasuredWidth(), childTop, childRight, childTop + secondaryTitle_TV.getMeasuredHeight());
        }

        // Sub title
        if (subTitle_TV.getVisibility() != GONE)
        {
            final LayoutParams params = (LayoutParams) subTitle_TV.getLayoutParams();

            final int childLeft = parentLeft + params.leftMargin;
            final int childTop = ((title_TV.getVisibility() != GONE || secondaryTitle_TV.getVisibility() != GONE) ? Math.max(title_TV.getVisibility() != GONE ? title_TV.getBottom() : 0, secondaryTitle_TV.getVisibility() != GONE ? secondaryTitle_TV.getBottom() : 0) : parentTop) + params.topMargin;
            subTitle_TV.layout(childLeft, childTop, childLeft + subTitle_TV.getMeasuredWidth(), childTop + subTitle_TV.getMeasuredHeight());
        }
    }

    public TextView getTitleView()
    {
        return title_TV;
    }

    public TextView getSecondaryTitleView()
    {
        return secondaryTitle_TV;
    }

    public TextView getSubTitleView()
    {
        return subTitle_TV;
    }

    public CharSequence getTitle()
    {
        return title_TV.getText();
    }

    public void setTitle(CharSequence title)
    {
        title_TV.setText(title);
        title_TV.setVisibility(TextUtils.isEmpty(title) ? GONE : VISIBLE);
    }

    @Override
    public void setCardInfo(CardInfo cardInfo)
    {
        super.setCardInfo(cardInfo);

        final TitleCardInfo info = (TitleCardInfo) cardInfo;
        setTitle(info.getTitle());
        setSecondaryTitle(info.getSecondaryTitle());
        setSubTitle(info.getSubTitle());
    }

    public void setTitle(int resId)
    {
        setTitle(getResources().getString(resId));
    }

    public CharSequence getSecondaryTitle()
    {
        return secondaryTitle_TV.getText();
    }

    public void setSecondaryTitle(CharSequence title)
    {
        secondaryTitle_TV.setText(title);
        secondaryTitle_TV.setVisibility(TextUtils.isEmpty(title) ? GONE : VISIBLE);
    }

    public void setSecondaryTitle(int resId)
    {
        setSecondaryTitle(getResources().getString(resId));
    }

    public CharSequence getSubTitle()
    {
        return subTitle_TV.getText();
    }

    public void setSubTitle(CharSequence title)
    {
        subTitle_TV.setText(title);
        subTitle_TV.setVisibility(TextUtils.isEmpty(title) ? GONE : VISIBLE);
    }

    public void setSubTitle(int resId)
    {
        setSubTitle(getResources().getString(resId));
    }

    protected int getContentTop()
    {
        int contentTop = Math.max(title_TV.getVisibility() != GONE ? title_TV.getBottom() + ((LayoutParams) title_TV.getLayoutParams()).bottomMargin : 0, secondaryTitle_TV.getVisibility() != GONE ? secondaryTitle_TV.getBottom() + ((LayoutParams) secondaryTitle_TV.getLayoutParams()).bottomMargin : 0);
        return Math.max(contentTop, subTitle_TV.getVisibility() != GONE ? subTitle_TV.getBottom() + ((LayoutParams) subTitle_TV.getLayoutParams()).bottomMargin : 0);
    }

    public static class TitleCardInfo extends CardInfo
    {
        protected CharSequence title;
        protected CharSequence secondaryTitle;
        protected CharSequence subTitle;

        public TitleCardInfo(long id)
        {
            super(id);
        }

        public CharSequence getTitle()
        {
            return title;
        }

        public TitleCardInfo setTitle(CharSequence title)
        {
            this.title = title;
            return this;
        }

        public CharSequence getSecondaryTitle()
        {
            return secondaryTitle;
        }

        public TitleCardInfo setSecondaryTitle(CharSequence secondaryTitle)
        {
            this.secondaryTitle = secondaryTitle;
            return this;
        }

        public CharSequence getSubTitle()
        {
            return subTitle;
        }

        public TitleCardInfo setSubTitle(CharSequence subTitle)
        {
            this.subTitle = subTitle;
            return this;
        }
    }
}
