package com.code44.finance.views.reports;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.code44.finance.R;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class BudgetView extends View
{
    private final Paint paint = new Paint();
    private int progressColor;
    private int bgColor;
    private float progress;

    public BudgetView(Context context)
    {
        this(context, null);
    }

    public BudgetView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BudgetView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        final Resources res = getResources();
        progressColor = res.getColor(R.color.f_brand);
        bgColor = res.getColor(R.color.f_light_darker2);
        progress = 0;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        final float width = getMeasuredWidth();
        final float height = getMeasuredHeight();
        final float progressWidth = width * progress;

        paint.setColor(progressColor);
        canvas.drawRect(0, 0, progressWidth, height, paint);

        paint.setColor(bgColor);
        canvas.drawRect(progressWidth, 0, width, height, paint);
    }

    public int getProgressColor()
    {
        return progressColor;
    }

    public void setProgressColor(int progressColor)
    {
        this.progressColor = progressColor;
        invalidate();
    }

    public int getBackgroundColor()
    {
        return bgColor;
    }

    public void setBackgroundColor(int backgroundColor)
    {
        this.bgColor = backgroundColor;
        invalidate();
    }

    public float getProgress()
    {
        return progress;
    }

    public void setProgress(float progress)
    {
        this.progress = Math.max(0, Math.min(1.0f, progress));
        invalidate();
    }
}
