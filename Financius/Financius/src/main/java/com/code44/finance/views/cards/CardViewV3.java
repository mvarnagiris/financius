package com.code44.finance.views.cards;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.TypefaceHelper;

import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class CardViewV3 extends ViewGroup
{
    protected final ImageView iconLeft_IV;
    protected final ImageView iconRight_IV;
    protected final TextView title_TV;
    protected final TextView topInfo_TV;
    protected final TextView subtitle_TV;
    protected final LinearLayout listContainer_LL;
    protected final TextView bottomInfo_TV;
    // -----------------------------------------------------------------------------------------------------------------
    protected Drawable cardDrawable;
    protected Drawable foregroundDrawable;
    protected View content_V;
    // -----------------------------------------------------------------------------------------------------------------
    protected int cardPaddingLeft;
    protected int cardPaddingRight;
    protected int cardPaddingTop;
    protected int cardPaddingBottom;


    public CardViewV3(Context context)
    {
        this(context, null);
    }

    public CardViewV3(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CardViewV3(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Init
        final Resources res = getResources();
        cardPaddingLeft = 0;
        cardPaddingRight = 0;
        cardPaddingTop = 0;
        cardPaddingBottom = 0;
        //noinspection ConstantConditions
        cardDrawable = res.getDrawable(R.drawable.bg_card_normal_new);
        foregroundDrawable = res.getDrawable(R.drawable.card_selector);

        // Setup
        final int padding = res.getDimensionPixelSize(R.dimen.space_normal);
        setPadding(padding, padding, padding, padding);
        setClipToPadding(false);

        MarginLayoutParams lp;

        // Icon left
        iconLeft_IV = new ImageView(context);
        lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.rightMargin = res.getDimensionPixelSize(R.dimen.space_normal);
        iconLeft_IV.setLayoutParams(lp);
        iconLeft_IV.setVisibility(GONE);

        // Icon right
        iconRight_IV = new ImageView(context);
        lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = res.getDimensionPixelSize(R.dimen.space_normal);
        iconRight_IV.setLayoutParams(lp);
        iconRight_IV.setVisibility(GONE);

        // Title
        title_TV = new TextView(context);
        lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        title_TV.setLayoutParams(lp);
        title_TV.setTextColor(res.getColor(R.color.text_primary));
        title_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.text_large));
        if (!isInEditMode())
            title_TV.setTypeface(TypefaceHelper.getTypeface(TypefaceHelper.TYPEFACE_SERIF));
        title_TV.setVisibility(GONE);

        // Top info
        topInfo_TV = new TextView(context);
        lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = res.getDimensionPixelSize(R.dimen.space_normal);
        topInfo_TV.setLayoutParams(lp);
        topInfo_TV.setTextColor(res.getColor(R.color.text_secondary));
        topInfo_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.text_small));
        topInfo_TV.setVisibility(GONE);

        // Subtitle
        subtitle_TV = new TextView(context);
        lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        subtitle_TV.setLayoutParams(lp);
        subtitle_TV.setTextColor(res.getColor(R.color.text_secondary));
        subtitle_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.text_normal));
        subtitle_TV.setVisibility(GONE);

        // List container
        listContainer_LL = new LinearLayout(context);
        lp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = res.getDimensionPixelSize(R.dimen.space_normal);
        listContainer_LL.setLayoutParams(lp);
        listContainer_LL.setVisibility(GONE);

        // Bottom info
        bottomInfo_TV = new TextView(context);
        lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        bottomInfo_TV.setLayoutParams(lp);
        bottomInfo_TV.setTextColor(res.getColor(R.color.text_secondary));
        bottomInfo_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.text_small));
        bottomInfo_TV.setVisibility(GONE);

        // Add views
        addView(iconLeft_IV);
        addView(iconRight_IV);
        addView(title_TV);
        addView(topInfo_TV);
        addView(subtitle_TV);
        addView(listContainer_LL);
        addView(bottomInfo_TV);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int unspecifiedMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        MarginLayoutParams lp;

        // Find width that is available for views
        final int contentWidth = width - getPaddingLeft() - getPaddingRight();

        // Icon left
        int iconLeftHeight = 0;
        int iconLeftWidth = 0;
        if (iconLeft_IV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) iconLeft_IV.getLayoutParams();
            final int wMS;
            final int hMS;

            //noinspection ConstantConditions
            if (lp.width >= 0)
                wMS = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            else
            {
                final int maxWidth = contentWidth - lp.leftMargin - lp.rightMargin;
                wMS = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
            }

            if (lp.height >= 0)
                hMS = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            else
                hMS = unspecifiedMS;

            iconLeft_IV.measure(wMS, hMS);
            iconLeftWidth = lp.leftMargin + iconLeft_IV.getMeasuredWidth() + lp.rightMargin;
            iconLeftHeight = lp.topMargin + iconLeft_IV.getMeasuredHeight() + lp.bottomMargin;
        }

        // Icon right
        int iconRightHeight = 0;
        int iconRightWidth = 0;
        if (iconRight_IV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) iconRight_IV.getLayoutParams();
            final int wMS;
            final int hMS;

            //noinspection ConstantConditions
            if (lp.width >= 0)
                wMS = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            else
            {
                final int maxWidth = contentWidth - lp.leftMargin - lp.rightMargin;
                wMS = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
            }

            if (lp.height >= 0)
                hMS = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            else
                hMS = unspecifiedMS;

            iconRight_IV.measure(wMS, hMS);
            iconRightWidth = lp.leftMargin + iconRight_IV.getMeasuredWidth() + lp.rightMargin;
            iconRightHeight = lp.topMargin + iconRight_IV.getMeasuredHeight() + lp.bottomMargin;
        }

        // Top info
        int topInfoHeight = 0;
        int topInfoWidth = 0;
        if (topInfo_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) topInfo_TV.getLayoutParams();
            int maxWidth = contentWidth - iconLeftWidth - iconRightWidth;
            if (title_TV.getVisibility() != GONE)
                maxWidth /= 2;
            //noinspection ConstantConditions
            maxWidth -= lp.leftMargin - lp.rightMargin;
            topInfo_TV.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), unspecifiedMS);
            topInfoHeight = lp.topMargin + topInfo_TV.getMeasuredHeight() + lp.bottomMargin;
            topInfoWidth = lp.leftMargin + topInfo_TV.getMeasuredWidth() + lp.rightMargin;
        }

        // Title
        int titleHeight = 0;
        if (title_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) title_TV.getLayoutParams();
            //noinspection ConstantConditions
            final int maxWidth = contentWidth - iconLeftWidth - iconRightWidth - topInfoWidth - lp.leftMargin - lp.rightMargin;
            title_TV.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), unspecifiedMS);
            titleHeight = lp.topMargin + title_TV.getMeasuredHeight() + lp.bottomMargin;
        }

        // Subtitle
        int subtitleHeight = 0;
        if (subtitle_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) subtitle_TV.getLayoutParams();
            final int maxWidth = contentWidth - iconLeftWidth - iconRightWidth - lp.leftMargin - lp.rightMargin;
            subtitle_TV.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), unspecifiedMS);
            subtitleHeight = lp.topMargin + subtitle_TV.getMeasuredHeight() + lp.bottomMargin;
        }

        // Content
        int contentHeight = 0;
        if (content_V != null && content_V.getVisibility() != GONE)
        {
            final LayoutParams layoutParams = content_V.getLayoutParams();
            lp = null;
            if (layoutParams instanceof MarginLayoutParams)
                lp = (MarginLayoutParams) layoutParams;
            final int wMS;
            final int hMS;

            if (layoutParams.width >= 0)
                wMS = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            else
            {
                final int maxWidth = contentWidth - iconLeftWidth - iconRightWidth - (lp != null ? lp.leftMargin + lp.rightMargin : 0);
                wMS = MeasureSpec.makeMeasureSpec(maxWidth, layoutParams.width == LayoutParams.MATCH_PARENT ? MeasureSpec.EXACTLY : MeasureSpec.AT_MOST);
            }

            if (layoutParams.height >= 0)
                hMS = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            else
                hMS = unspecifiedMS;

            content_V.measure(wMS, hMS);
            contentHeight = content_V.getMeasuredHeight() + (lp != null ? lp.topMargin + lp.bottomMargin : 0);
        }

        // List
        int listHeight = 0;
        if (listContainer_LL.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) listContainer_LL.getLayoutParams();
            final int wMS;
            final int hMS;

            if (lp.width >= 0)
                wMS = MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY);
            else
            {
                final int maxWidth = width - getCardPaddingLeft() - getCardPaddingRight() - lp.leftMargin - lp.rightMargin;
                wMS = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
            }

            if (lp.height >= 0)
                hMS = MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY);
            else
                hMS = unspecifiedMS;

            listContainer_LL.measure(wMS, hMS);
            listHeight = lp.topMargin + listContainer_LL.getMeasuredHeight() + lp.bottomMargin;
        }

        // Bottom info
        int bottomInfoHeight = 0;
        int bottomInfoWidth = 0;
        if (bottomInfo_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) bottomInfo_TV.getLayoutParams();
            final int maxWidth = contentWidth - iconLeftWidth - iconRightWidth - lp.leftMargin - lp.rightMargin;
            bottomInfo_TV.measure(MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST), unspecifiedMS);
            bottomInfoHeight = lp.topMargin + bottomInfo_TV.getMeasuredHeight() + lp.bottomMargin;
        }

        int height = Math.max(titleHeight, topInfoHeight);
        height += subtitleHeight;
        height += contentHeight;
        if (listContainer_LL.getVisibility() == GONE)
            height += bottomInfoHeight;
        height = Math.max(Math.max(height, iconLeftHeight), iconRightHeight);
        height += listHeight;
        if (listContainer_LL.getVisibility() != GONE)
            height += bottomInfoHeight;
        height += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (cardDrawable != null)
            cardDrawable.setBounds(getCardPaddingLeft(), getCardPaddingTop(), w - getCardPaddingRight(), h - getCardPaddingBottom());

        if (foregroundDrawable != null)
            foregroundDrawable.setBounds(getCardPaddingLeft(), getCardPaddingTop(), w - getCardPaddingRight(), h - getCardPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        final int width = getMeasuredWidth();
        MarginLayoutParams lp;
        int top = getPaddingTop();
        final int left = getPaddingLeft();

        // Icon left
        int iconLeftBottom = top;
        int iconLeftRight = left;
        if (iconLeft_IV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) iconLeft_IV.getLayoutParams();
            //noinspection ConstantConditions
            final int iconLeftTop = top + lp.topMargin;
            final int iconLeftLeft = left + lp.leftMargin;
            iconLeft_IV.layout(iconLeftLeft, iconLeftTop, iconLeftLeft + iconLeft_IV.getMeasuredWidth(), iconLeftTop + iconLeft_IV.getMeasuredHeight());
            iconLeftBottom = iconLeft_IV.getBottom() + lp.bottomMargin;
            iconLeftRight = iconLeft_IV.getRight() + lp.rightMargin;
        }

        // Icon right
        int iconRightBottom = top;
        if (iconRight_IV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) iconRight_IV.getLayoutParams();
            //noinspection ConstantConditions
            final int iconRightTop = top + lp.topMargin;
            final int iconRightRight = width - getPaddingRight() - lp.rightMargin;
            iconRight_IV.layout(iconRightRight - iconRight_IV.getMeasuredWidth(), iconRightTop, iconRightRight, iconRightTop + iconRight_IV.getMeasuredHeight());
            iconRightBottom = iconRight_IV.getBottom() + lp.bottomMargin;
        }

        // Title
        int titleBottom = top;
        if (title_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) title_TV.getLayoutParams();
            //noinspection ConstantConditions
            final int titleTop = top + lp.topMargin;
            final int titleLeft = iconLeftRight + lp.leftMargin;
            title_TV.layout(titleLeft, titleTop, titleLeft + title_TV.getMeasuredWidth(), titleTop + title_TV.getMeasuredHeight());
            titleBottom = title_TV.getBottom() + lp.bottomMargin;
        }

        // Top info
        int topInfoBottom = top;
        if (topInfo_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) topInfo_TV.getLayoutParams();
            //noinspection ConstantConditions
            final int topInfoTop = top + lp.topMargin;
            final int topInfoRight = width - getPaddingRight() - lp.rightMargin;
            topInfo_TV.layout(topInfoRight - topInfo_TV.getMeasuredWidth(), topInfoTop, topInfoRight, topInfoTop + topInfo_TV.getMeasuredHeight());
            topInfoBottom = topInfo_TV.getBottom() + lp.bottomMargin;
        }

        // Subtitle
        int subtitleBottom = Math.max(titleBottom, topInfoBottom);
        if (subtitle_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) subtitle_TV.getLayoutParams();
            //noinspection ConstantConditions
            final int subtitleTop = Math.max(titleBottom, topInfoBottom) + lp.topMargin;
            final int subtitleLeft = iconLeftRight + lp.leftMargin;
            subtitle_TV.layout(subtitleLeft, subtitleTop, subtitleLeft + subtitle_TV.getMeasuredWidth(), subtitleTop + subtitle_TV.getMeasuredHeight());
            subtitleBottom = subtitle_TV.getBottom() + lp.bottomMargin;
        }

        // Content
        int contentBottom = subtitleBottom;
        if (content_V != null && content_V.getVisibility() != GONE)
        {
            lp = null;
            if (content_V.getLayoutParams() instanceof MarginLayoutParams)
                lp = (MarginLayoutParams) content_V.getLayoutParams();
            //noinspection ConstantConditions
            final int contentTop = subtitleBottom + (lp != null ? lp.topMargin : 0);
            final int contentLeft = iconLeftRight + (lp != null ? lp.leftMargin : 0);
            content_V.layout(contentLeft, contentTop, contentLeft + content_V.getMeasuredWidth(), contentTop + content_V.getMeasuredHeight());
            contentBottom = content_V.getBottom() + (lp != null ? lp.bottomMargin : 0);
        }

        // List
        int listBottom = Math.max(Math.max(contentBottom, iconLeftBottom), iconRightBottom);
        if (listContainer_LL.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) listContainer_LL.getLayoutParams();
            //noinspection ConstantConditions
            final int listTop = contentBottom + lp.topMargin;
            final int listLeft = getCardPaddingLeft() + lp.leftMargin;
            listContainer_LL.layout(listLeft, listTop, listLeft + listContainer_LL.getMeasuredWidth(), listTop + listContainer_LL.getMeasuredHeight());
            listBottom = listContainer_LL.getBottom() + lp.bottomMargin;
        }

        // Bottom info
        if (bottomInfo_TV.getVisibility() != GONE)
        {
            lp = (MarginLayoutParams) bottomInfo_TV.getLayoutParams();
            //noinspection ConstantConditions
            final int bottomInfoTop = (listContainer_LL.getVisibility() != GONE ? listBottom : contentBottom) + lp.topMargin;
            final int bottomInfoLeft = iconLeftRight + lp.leftMargin;
            bottomInfo_TV.layout(bottomInfoLeft, bottomInfoTop, bottomInfoLeft + bottomInfo_TV.getMeasuredWidth(), bottomInfoTop + bottomInfo_TV.getMeasuredHeight());
        }
    }

    public void setTitle(int resId)
    {
        //noinspection ConstantConditions
        setTitle(getContext().getString(resId));
    }

    public void setTitleVisibility(int visibility)
    {
        title_TV.setVisibility(visibility);
    }

    public CharSequence getTitle()
    {
        if (title_TV.getText() == null)
            return null;
        return title_TV.getText();
    }

    public void setTitle(CharSequence title)
    {
        title_TV.setText(title);
    }

    public void setTopInfo(int resId)
    {
        //noinspection ConstantConditions
        setTopInfo(getContext().getString(resId));
    }

    public void setTopInfoVisibility(int visibility)
    {
        topInfo_TV.setVisibility(visibility);
    }

    public CharSequence getTopInfo()
    {
        if (topInfo_TV.getText() == null)
            return null;
        return topInfo_TV.getText();
    }

    public void setTopInfo(CharSequence topInfo)
    {
        topInfo_TV.setText(topInfo);
    }

    public void setSubtitle(int resId)
    {
        //noinspection ConstantConditions
        setSubtitle(getContext().getString(resId));
    }

    public void setSubtitleVisibility(int visibility)
    {
        subtitle_TV.setVisibility(visibility);
    }

    public CharSequence getSubtitle()
    {
        if (subtitle_TV.getText() == null)
            return null;
        return subtitle_TV.getText();
    }

    public void setSubtitle(CharSequence subtitle)
    {
        subtitle_TV.setText(subtitle);
    }

    public void setIconLeft(int resId)
    {
        iconLeft_IV.setImageResource(resId);
    }

    public void setIconLeftVisibility(int visibility)
    {
        iconLeft_IV.setVisibility(visibility);
    }

    public void setIconRight(int resId)
    {
        iconRight_IV.setImageResource(resId);
    }

    public void setIconRightVisibility(int visibility)
    {
        iconRight_IV.setVisibility(visibility);
    }

    public void setList(List<View> viewList)
    {
        listContainer_LL.removeAllViews();
        if (viewList != null)
        {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < viewList.size(); i++)
                listContainer_LL.addView(viewList.get(i));
        }
    }

    public void setListVisibility(int visibility)
    {
        listContainer_LL.setVisibility(visibility);
    }

    public void setBottomInfo(int resId)
    {
        //noinspection ConstantConditions
        setBottomInfo(getContext().getString(resId));
    }

    public void setBottomInfoVisibility(int visibility)
    {
        bottomInfo_TV.setVisibility(visibility);
    }

    public String getBottomInfo()
    {
        if (bottomInfo_TV.getText() == null)
            return null;
        return bottomInfo_TV.getText().toString();
    }

    public void setBottomInfo(String title)
    {
        bottomInfo_TV.setText(title);
    }

    public void setContent(int layoutId)
    {
        //noinspection ConstantConditions
        setContent(LayoutInflater.from(getContext()).inflate(layoutId, this, false));
    }

    public void setContent(View view)
    {
        if (content_V != null)
            removeView(content_V);

        content_V = view;
        addView(content_V);
    }

    public View getContentView()
    {
        return content_V;
    }

    @Override
    public int getPaddingLeft()
    {
        return super.getPaddingLeft() + getCardPaddingLeft();
    }

    @Override
    public int getPaddingRight()
    {
        return super.getPaddingRight() + getCardPaddingRight();
    }

    @Override
    public int getPaddingTop()
    {
        return super.getPaddingTop() + getCardPaddingTop();
    }

    @Override
    public int getPaddingBottom()
    {
        return super.getPaddingBottom() + getCardPaddingBottom();
    }

    public int getCardPaddingLeft()
    {
        return cardPaddingLeft;
    }

    public void setCardPaddingLeft(int cardPaddingLeft)
    {
        this.cardPaddingLeft = cardPaddingLeft;
    }

    public int getCardPaddingRight()
    {
        return cardPaddingRight;
    }

    public void setCardPaddingRight(int cardPaddingRight)
    {
        this.cardPaddingRight = cardPaddingRight;
    }

    public int getCardPaddingTop()
    {
        return cardPaddingTop;
    }

    public void setCardPaddingTop(int cardPaddingTop)
    {
        this.cardPaddingTop = cardPaddingTop;
    }

    public int getCardPaddingBottom()
    {
        return cardPaddingBottom;
    }

    public void setCardPaddingBottom(int cardPaddingBottom)
    {
        this.cardPaddingBottom = cardPaddingBottom;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        if (cardDrawable != null)
            cardDrawable.draw(canvas);

        if (foregroundDrawable != null)
            foregroundDrawable.draw(canvas);
        super.dispatchDraw(canvas);
    }

    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();

        foregroundDrawable.setState(getDrawableState());

        //redraw
        invalidate();
    }
}