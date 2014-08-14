package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartView;
import com.code44.finance.utils.MoneyFormatter;

public class OverviewGraphView extends LinearLayout {
    private final PieChartView pieChart_V;
    private final TextView totalExpense_TV;

    @SuppressWarnings("UnusedDeclaration")
    public OverviewGraphView(Context context) {
        this(context, null);
    }

    public OverviewGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverviewGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public OverviewGraphView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.v_overview_graph, this);
        setBackgroundResource(R.drawable.btn_borderless);

        // Get views
        pieChart_V = (PieChartView) findViewById(R.id.pieChart_V);
        totalExpense_TV = (TextView) findViewById(R.id.totalExpense_TV);

        // Setup
        pieChart_V.setEmptyColor(totalExpense_TV.getCurrentTextColor());
        setPieChartData(null);
        if (isInEditMode()) {
            totalExpense_TV.setText("0.00 $");
        } else {
            setTotalExpense(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final LayoutParams params = (LayoutParams) pieChart_V.getLayoutParams();
        params.height = pieChart_V.getMeasuredWidth();
    }

    public void setPieChartData(PieChartData pieChartData) {
        pieChart_V.setPieChartData(pieChartData);
    }

    public void setTotalExpense(long totalExpense) {
        totalExpense_TV.setText(MoneyFormatter.format(Currency.getDefault(), totalExpense));
    }
}
