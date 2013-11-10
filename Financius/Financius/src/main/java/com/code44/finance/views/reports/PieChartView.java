package com.code44.finance.views.reports;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.code44.finance.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mantas on 02/06/13.
 */
public class PieChartView extends View
{
    private final List<PieChartItem> itemList = new ArrayList<PieChartItem>();
    private final RectF rect = new RectF();
    private final RectF strokeRect = new RectF();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint emptySrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int cutOffIndex = -1;

    public PieChartView(Context context)
    {
        this(context, null);
    }

    public PieChartView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PieChartView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Init
        final Resources res = getResources();
        setStrokeColor(res.getColor(R.color.bg_primary));
        setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, res.getDisplayMetrics()));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        final Bitmap fillBMP = BitmapFactory.decodeResource(getResources(), R.drawable.pattern_stripe);
        final BitmapShader fillBMPShader = new BitmapShader(fillBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        emptyPaint.setShader(fillBMPShader);
        setPieChartType(PieChartType.DONUT);
        setDonutWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15.0f, res.getDisplayMetrics()));
        emptySrokePaint.setColor(res.getColor(R.color.f_light_darker2));
        emptySrokePaint.setStyle(Paint.Style.STROKE);
        emptySrokePaint.setStrokeWidth(strokePaint.getStrokeWidth());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int size = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.UNSPECIFIED ? Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec)) : MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        final int contentW = w - getPaddingLeft() - getPaddingRight();
        final int contentH = h - getPaddingTop() - getPaddingBottom();
        final int centerX = w / 2;
        final int centerY = h / 2;
        final int contentSize = Math.min(contentW, contentH);
        final float strokeSizeHalf = contentSize;
        final float sizeHalf;
        if (paint.getStyle() == Paint.Style.STROKE)
        {
            // Donut style
            sizeHalf = (contentSize - paint.getStrokeWidth()) / 2.0f;
        }
        else
        {
            // Pie style
            sizeHalf = contentSize / 2.0f;
        }

        rect.set(centerX - sizeHalf, centerY - sizeHalf, centerX + sizeHalf, centerY + sizeHalf);
        strokeRect.set(centerX - strokeSizeHalf, centerY - strokeSizeHalf, centerX + strokeSizeHalf, centerY + strokeSizeHalf);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        //Prepare values
        final boolean useCenter = paint.getStyle() == Paint.Style.FILL;

        float startAngle = -90.0f;
        float sweepAngle;

        if (itemList.size() > 0)
        {
            PieChartItem item;
            for (int i = 0; i < itemList.size(); i++)
            {
                if (i == cutOffIndex - 1)
                {
                    final Paint.Style emptyPaintStyle = emptyPaint.getStyle();
                    emptyPaint.setStyle(paint.getStyle());
                    sweepAngle = 360.0f - startAngle - 90.0f;
                    canvas.drawArc(rect, startAngle, sweepAngle, useCenter, emptyPaint);
                    canvas.drawArc(strokeRect, startAngle, sweepAngle, true, strokePaint);
                    emptyPaint.setStyle(emptyPaintStyle);
                    break;
                }
                item = itemList.get(i);
                sweepAngle = 360.0f * item.getPieChartFraction();
                paint.setColor(item.getPieChartColor());
                canvas.drawArc(rect, startAngle, sweepAngle, useCenter, paint);
                canvas.drawArc(strokeRect, startAngle, sweepAngle, true, strokePaint);
                startAngle += sweepAngle;
            }
        }
        else
        {
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f, emptyPaint);
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2.0f + paint.getStrokeWidth() / 2.0f - emptySrokePaint.getStrokeWidth() / 2.0f, emptySrokePaint);
        }
    }

    public void bind(List<? extends PieChartItem> itemList)
    {
        this.itemList.clear();
        if (itemList != null)
            this.itemList.addAll(itemList);

        invalidate();
    }

    public void setPieChartType(PieChartType type)
    {
        switch (type)
        {
            case PIE:
                paint.setStyle(Paint.Style.FILL);
                emptyPaint.setStyle(Paint.Style.FILL);
                break;

            case DONUT:
                paint.setStyle(Paint.Style.STROKE);
                emptyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                break;
        }
        requestLayout();
    }

    public void setDonutWidth(float width)
    {
        paint.setStrokeWidth(width);
        emptyPaint.setStrokeWidth(width);
        requestLayout();
    }

    public void setStrokeWidth(float width)
    {
        strokePaint.setStrokeWidth(width);
        invalidate();
    }

    public void setStrokeColor(int color)
    {
        strokePaint.setColor(color);
    }

    /**
     * After cutoff index items will be grouped. Pass -1 to remove grouping.
     *
     * @param cutOffIndex Index after which items will be grouped.
     */
    public void setCutOffIndex(int cutOffIndex)
    {
        this.cutOffIndex = cutOffIndex;
        invalidate();
    }

    public enum PieChartType
    {
        PIE, DONUT
    }

    public static interface PieChartItem
    {
        public int getPieChartColor();

        public float getPieChartFraction();
    }
}
