package com.code44.finance.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;

public class FilterToggleView extends LinearLayout
{
    protected TextView title_TV;
    protected TextView description_TV;
    protected ImageButton clear_B;
    // -----------------------------------------------------------------------------------------------------------------
    protected Drawable foregroundSelector;
    protected Callbacks callbacks;

    public FilterToggleView(Context context)
    {
        this(context, null);
    }

    public FilterToggleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FilterToggleView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflate(context, R.layout.v_filter_toggle, this);

        // Get views
        title_TV = (TextView) findViewById(R.id.title_TV);
        description_TV = (TextView) findViewById(R.id.description_TV);
        clear_B = (ImageButton) findViewById(R.id.clear_B);

        // Setup
        final Resources res = getResources();
        //noinspection ConstantConditions
        foregroundSelector = res.getDrawable(R.drawable.btn_borderless);
        setOrientation(HORIZONTAL);
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        setDividerPadding(res.getDimensionPixelSize(R.dimen.divider_padding));
        clear_B.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (callbacks != null)
                    callbacks.onFilterClearClick(FilterToggleView.this);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        foregroundSelector.setBounds(0, 0, w, h);
    }

    public void setTitle(String title)
    {
        title_TV.setText(title);
    }

    public void setDescription(String description)
    {
        description_TV.setText(description);
    }

    public void setFilterSet(boolean filterSet)
    {
        clear_B.setVisibility(filterSet ? VISIBLE : GONE);
    }

    public void setCallbacks(Callbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();

        foregroundSelector.setState(getDrawableState());
        invalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        foregroundSelector.draw(canvas);
    }

    public static interface Callbacks
    {
        public void onFilterClearClick(FilterToggleView v);
    }
}