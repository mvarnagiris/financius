package com.code44.finance.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.code44.finance.R;

/**
 * Created by Mantas on 05/06/13.
 */
public class ColorView extends View
{
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ColorView(Context context)
    {
        this(context, null);
    }

    public ColorView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ColorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        final Resources res = getResources();
        final int padding = res.getDimensionPixelSize(R.dimen.space_normal);
        setPadding(padding, padding, padding, padding);
    }

    public void bind(int color)
    {
        paint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int size = getMeasuredWidth();
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (getMeasuredWidth() - getPaddingLeft() - getPaddingTop()) / 2, paint);
    }
}
