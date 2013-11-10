package com.code44.finance.views.reports;

import android.content.Context;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.utils.PeriodHelper;

import java.util.ArrayList;
import java.util.List;

public class ExpenseGraphView extends View
{
    protected static final float STROKE_WIDTH_DP = 1.0f;
    protected static final float CIRCLE_RADIUS_DP = 2f;
    protected final float STROKE_WIDTH;
    protected final float CIRCLE_RADIUS;
    protected final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Paint lineCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Path linePath = new Path();
    protected final Paint lineBreakPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Paint lineBreakCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Path lineBreakPath = new Path();
    protected final Paint areaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    protected final Path areaPath = new Path();
    protected final List<PointF> itemPositionList = new ArrayList<PointF>();
    protected final List<Float> itemList = new ArrayList<Float>();
    protected int currentItemIndex = 0;

    public ExpenseGraphView(Context context)
    {
        this(context, null);
    }

    public ExpenseGraphView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ExpenseGraphView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Init
        STROKE_WIDTH = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH_DP, getResources().getDisplayMetrics());
        CIRCLE_RADIUS = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_RADIUS_DP, getResources().getDisplayMetrics());

        linePaint.setColor(getResources().getColor(R.color.text_red));
        linePaint.setStyle(Style.STROKE);
        linePaint.setStrokeWidth(STROKE_WIDTH);
        lineCirclePaint.setColor(getResources().getColor(R.color.text_red));
        lineCirclePaint.setStyle(Style.FILL);

        lineBreakPaint.setColor(getResources().getColor(R.color.f_light_darker2));
        lineBreakPaint.setStyle(Style.STROKE);
        lineBreakPaint.setStrokeWidth(STROKE_WIDTH);
        lineBreakCirclePaint.setColor(getResources().getColor(R.color.f_light_darker2));
        lineBreakCirclePaint.setStyle(Style.FILL);

        final Bitmap fillBMP = BitmapFactory.decodeResource(getResources(), R.drawable.pattern_stripe);
        final BitmapShader fillBMPShader = new BitmapShader(fillBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        areaPaint.setStyle(Style.FILL);
        areaPaint.setShader(fillBMPShader);
    }

    public static List<Float> prepareItems(int periodType, long periodStart, long periodEnd, Cursor c)
    {
        // Prepare values
        final List<Float> itemList = new ArrayList<Float>();
        final int itemsCounts = PeriodHelper.getItemsCounts(periodType, periodStart);
        float maxValue = 0;
        float minValue = Float.MAX_VALUE;
        float expense = 0;

        if (c != null && c.moveToFirst())
        {
            int index;
            int currentIndex = 0;
            do
            {
                if (c.getFloat(2) > 0)
                {
                    // Find item index
                    if (periodType == PeriodHelper.TYPE_YEAR)
                        index = PeriodHelper.getMonthCountInPeriod(periodStart, c.getLong(0));
                    else if (periodType == PeriodHelper.TYPE_DAY)
                        index = PeriodHelper.getHourCountInPeriod(periodStart, c.getLong(0));
                    else
                        index = PeriodHelper.getDayCountInPeriod(periodStart, c.getLong(0));
                    index--;

                    // Add expense value to all preceding indexes that have not been filled
                    if (currentIndex < index)
                    {
                        // Update min and max values
                        if (expense > maxValue)
                            maxValue = expense;
                        if (expense < minValue)
                            minValue = expense;

                        while (currentIndex < index)
                        {
                            itemList.add(expense);
                            currentIndex++;
                        }
                    }

                    expense += c.getFloat(2);

                    // Update min and max values
                    if (expense > maxValue)
                        maxValue = expense;
                    if (expense < minValue)
                        minValue = expense;

                    // Add new item
                    if (index >= 0)
                    {
                        itemList.add(expense);
                        currentIndex = index + 1;
                    }
                }
            }
            while (c.moveToNext() && currentIndex < itemsCounts);
        }

        // Update min and max values
        if (expense > maxValue)
            maxValue = expense;
        if (expense < minValue)
            minValue = expense;

        // Fill remaining spaces with expense
        while (itemList.size() < itemsCounts)
            itemList.add(expense);

        // Update values based on min and max to be in range [0..1].
        if (maxValue > 0 && minValue < maxValue)
        {
            // We have days with values and max > min
            for (int i = 0; i < itemList.size(); i++)
            {
                itemList.set(i, (itemList.get(i) - minValue) / (maxValue - minValue));
            }
        }
        else
        {
            // The line in graph should be flat.
            for (int i = 0; i < itemList.size(); i++)
                itemList.set(i, 0.0f);
        }

        return itemList;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        updateValues();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (itemList.size() < 2)
            return;

        canvas.drawPath(areaPath, areaPaint);
        canvas.drawPath(linePath, linePaint);
        canvas.drawPath(lineBreakPath, lineBreakPaint);

        PointF point;
        for (int i = 0; i < itemPositionList.size(); i++)
        {
            point = itemPositionList.get(i);
            canvas.drawCircle(point.x, point.y, CIRCLE_RADIUS, i <= currentItemIndex ? lineCirclePaint : lineBreakCirclePaint);
        }
    }

    public void bind(List<Float> itemList, int currentItemIndex)
    {
        this.itemList.clear();
        if (itemList != null)
            this.itemList.addAll(itemList);
        this.currentItemIndex = currentItemIndex;

        updateValues();
        invalidate();
    }

    protected void updateValues()
    {
        final int width = (int) (getMeasuredWidth() - (CIRCLE_RADIUS * 4));
        final int height = (int) (getMeasuredHeight() - (CIRCLE_RADIUS * 4));
        final int size = itemList.size();
        if (width <= 0 || size < 2)
            return;

        final float stepSize = (float) width / (float) (size - 1);
        final float bottom = getMeasuredHeight() - (CIRCLE_RADIUS * 2);
        float left = CIRCLE_RADIUS * 2;
        itemPositionList.clear();
        for (int i = 0; i < size; i++)
        {
            itemPositionList.add(new PointF(left, bottom - (itemList.get(i) * height)));
            left += stepSize;
        }

        linePath.reset();
        lineBreakPath.reset();
        areaPath.reset();
        linePath.moveTo(CIRCLE_RADIUS * 2, bottom);
        areaPath.moveTo(CIRCLE_RADIUS * 2, bottom);

        PointF point = null;
        for (int i = 1; i < size; i++)
        {
            point = itemPositionList.get(i);
            if (i <= currentItemIndex)
                linePath.lineTo(point.x, point.y);
            else
            {
                if (lineBreakPath.isEmpty())
                    lineBreakPath.moveTo(point.x, point.y);
                else
                    lineBreakPath.lineTo(point.x, point.y);
            }
            areaPath.lineTo(point.x, point.y);
        }

        areaPath.lineTo(point.x, bottom);
        areaPath.close();
    }
}