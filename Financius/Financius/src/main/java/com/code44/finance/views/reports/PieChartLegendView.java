package com.code44.finance.views.reports;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mantas on 02/06/13.
 */
public class PieChartLegendView extends LinearLayout
{
    private final List<PieChartLegendItem> itemList = new ArrayList<PieChartLegendItem>();
    private final float itemHeight;
    private PieChartView pieChart_V;
    private int maxItemsCount = 0;

    public PieChartLegendView(Context context)
    {
        this(context, null);
    }

    public PieChartLegendView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PieChartLegendView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup layout
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        // Init
        View tempView = LayoutInflater.from(context).inflate(R.layout.v_pie_chart_legend, this, false);
        ((TextView) tempView.findViewById(R.id.title_TV)).setText("W");
        tempView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        itemHeight = tempView.getMeasuredHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate how many items fit in layout
        maxItemsCount = (int) Math.floor((double) h / itemHeight);
        if (maxItemsCount > 0)
        {
            post(new Runnable()
            {
                @Override
                public void run()
                {
                    updateViews();
                }
            });
        }
    }

    public void bind(List<? extends PieChartLegendItem> itemList)
    {
        this.itemList.clear();
        if (itemList != null)
            this.itemList.addAll(itemList);
        post(new Runnable()
        {
            @Override
            public void run()
            {
                updateViews();
            }
        });
    }

    public void setPieChartView(PieChartView pieChart_V)
    {
        this.pieChart_V = pieChart_V;
    }

    private void updateViews()
    {
        removeAllViews();
        if (itemList.size() == 0 || maxItemsCount == 0)
        {
            if (pieChart_V != null)
                pieChart_V.bind(itemList);
            return;
        }

        // Calculate the size of legend
        final int size = Math.min(itemList.size(), maxItemsCount);

        int cutoffIndex = -1;
        PieChartLegendItem item;
        View view;
        final long mainCurrencyId = CurrenciesHelper.getDefault().getMainCurrencyId();
        for (int i = 0; i < size; i++)
        {
            // Get item
            item = itemList.get(i);

            // Inflate new view
            view = LayoutInflater.from(getContext()).inflate(R.layout.v_pie_chart_legend, this, false);

            if (i == size - 1 && maxItemsCount < itemList.size())
            {
                // If this is the last item in legend, but the size should be bigger, just sum the rest
                float total = item.getPieChartLegendValue();
                i++;
                for (int e = i; e < itemList.size(); e++)
                {
                    total += itemList.get(e).getPieChartLegendValue();
                }
                ((TextView) view.findViewById(R.id.title_TV)).setText(R.string.other);
                ((TextView) view.findViewById(R.id.amount_TV)).setText(AmountUtils.formatAmount(mainCurrencyId, total));
                view.findViewById(R.id.color_V).setBackgroundResource(R.drawable.bg_stripe);
                cutoffIndex = maxItemsCount;
            }
            else
            {
                // Normal view
                ((TextView) view.findViewById(R.id.title_TV)).setText(item.getPieChartLegendTitle());
                ((TextView) view.findViewById(R.id.amount_TV)).setText(AmountUtils.formatAmount(mainCurrencyId, item.getPieChartLegendValue()));
                view.findViewById(R.id.color_V).setBackgroundColor(item.getPieChartColor());
            }

            // Add view to layout
            addView(view);
        }

        if (pieChart_V != null)
        {
            pieChart_V.setCutOffIndex(cutoffIndex);
            pieChart_V.bind(itemList);
        }
    }

    public static interface PieChartLegendItem extends PieChartView.PieChartItem
    {
        public String getPieChartLegendTitle();

        public float getPieChartLegendValue();
    }
}
