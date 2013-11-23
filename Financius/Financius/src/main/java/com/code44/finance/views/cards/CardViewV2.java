package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;

@SuppressWarnings("UnusedDeclaration")
public class CardViewV2 extends FrameLayout
{
    protected final View container_V;
    protected final TextView title_TV;
    protected final TextView secondaryTitle_TV;
    protected final TextView subTitle_TV;
    protected final ImageView icon_IV;
    protected final FrameLayout content_FL;
    protected final LinearLayout listContainer_LL;

    public CardViewV2(Context context)
    {
        this(context, null);
    }

    public CardViewV2(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CardViewV2(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflate(context, R.layout.v_card, this);

        // Setup background
        setBackgroundResource(R.drawable.bg_card_normal_new);
        //noinspection ConstantConditions
        setForeground(getResources().getDrawable(R.drawable.card_selector));

        // Get views
        container_V = findViewById(R.id.container_V);
        title_TV = (TextView) findViewById(R.id.title_TV);
        secondaryTitle_TV = (TextView) findViewById(R.id.secondaryTitle_TV);
        subTitle_TV = (TextView) findViewById(R.id.subTitle_TV);
        icon_IV = (ImageView) findViewById(R.id.icon_IV);
        content_FL = (FrameLayout) findViewById(R.id.content_FL);
        listContainer_LL = (LinearLayout) findViewById(R.id.listContainer_LL);

        // Setup
        icon_IV.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    public void setContentView(int resId)
    {
        View view = null;
        if (resId > 0)
            view = LayoutInflater.from(getContext()).inflate(resId, content_FL, false);
        setContentView(view);
    }

    public void setContentView(View view)
    {
        content_FL.removeAllViews();
        if (view != null)
        {
            content_FL.setVisibility(VISIBLE);
            content_FL.addView(view);
        }
        else
        {
            content_FL.setVisibility(GONE);
        }

        updateMargins();
    }

    public void setIcon(Drawable drawable)
    {
        if (drawable != null)
        {
            icon_IV.setVisibility(VISIBLE);
            icon_IV.setImageDrawable(drawable);
        }
        else
        {
            icon_IV.setVisibility(GONE);
            icon_IV.setImageDrawable(null);
        }

        updateMargins();
    }

    protected void updateMargins()
    {
        //noinspection ConstantConditions
        final int margin = getResources().getDimensionPixelSize(R.dimen.space_normal);

        final boolean titleVisible = title_TV.getVisibility() == VISIBLE;
        final boolean secondaryTitleVisible = secondaryTitle_TV.getVisibility() == VISIBLE;
        final boolean subTitleVisible = subTitle_TV.getVisibility() == VISIBLE;
        final boolean contentVisible = content_FL.getVisibility() == VISIBLE;
        final boolean iconVisible = icon_IV.getVisibility() == VISIBLE;

        MarginLayoutParams lp = (MarginLayoutParams) subTitle_TV.getLayoutParams();
        //noinspection ConstantConditions
        lp.topMargin = titleVisible || secondaryTitleVisible ? margin : 0;

        lp = (MarginLayoutParams) content_FL.getLayoutParams();
        //noinspection ConstantConditions
        lp.topMargin = titleVisible || secondaryTitleVisible || subTitleVisible ? margin : 0;

        lp = (MarginLayoutParams) icon_IV.getLayoutParams();
        //noinspection ConstantConditions
        lp.topMargin = titleVisible || secondaryTitleVisible || subTitleVisible ? margin : 0;

        lp = (MarginLayoutParams) listContainer_LL.getLayoutParams();
        //noinspection ConstantConditions
        lp.topMargin = titleVisible || secondaryTitleVisible || subTitleVisible || contentVisible || iconVisible ? margin : 0;
    }
}
