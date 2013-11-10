package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.model.CategoriesPeriodReport;
import com.code44.finance.views.reports.PieChartLegendView;
import com.code44.finance.views.reports.PieChartView;

/**
 * Created by Mantas on 25/05/13.
 */
public class ReportsCardView extends LinearLayout
{
    private final TextView period_TV;
    private final PieChartLegendView pieChartLegend_V;
    private final TextView empty_TV;

    public ReportsCardView(Context context)
    {
        this(context, null);
    }

    public ReportsCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ReportsCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflate(context, R.layout.v_reports_card, this);

        // Setup layout
        setOrientation(VERTICAL);

        // Setup card
        setBackgroundResource(R.drawable.bg_card_old);
        final int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        setPadding(getPaddingLeft() + padding, getPaddingTop() + padding, getPaddingRight() + padding, getPaddingBottom() + padding);

        // Get views
        final PieChartView pieChart_V = (PieChartView) findViewById(R.id.pieChart_V);
        period_TV = (TextView) findViewById(R.id.period_TV);
        pieChartLegend_V = (PieChartLegendView) findViewById(R.id.pieChartLegend_V);
        empty_TV = (TextView) findViewById(R.id.empty_TV);

        // Setup
        pieChartLegend_V.setPieChartView(pieChart_V);
    }

    public void bind(String period, CategoriesPeriodReport report)
    {
        period_TV.setText(period);
        pieChartLegend_V.bind(report.getExpenseList());
        empty_TV.setVisibility(report.getTotalExpenseItemsCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
