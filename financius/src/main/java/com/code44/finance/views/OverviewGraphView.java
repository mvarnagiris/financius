package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartView;
import com.code44.finance.qualifiers.Main;
import com.code44.finance.utils.LayoutType;
import com.code44.finance.utils.MoneyFormatter;

import javax.inject.Inject;

public class OverviewGraphView extends LinearLayout {
    @Inject @Main Currency mainCurrency;
    @Inject LayoutType layoutType;

    private PieChartView pieChartView;
    private TextView totalExpenseView;

    public OverviewGraphView(Context context) {
        this(context, null);
    }

    public OverviewGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverviewGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        // Get views
        final TextView titleView = (TextView) findViewById(R.id.title);
        pieChartView = (PieChartView) findViewById(R.id.pieChart);
        totalExpenseView = (TextView) findViewById(R.id.totalExpense);

        // Setup
        pieChartView.setEmptyColor(totalExpenseView.getCurrentTextColor());
        setPieChartData(null);
        if (isInEditMode()) {
            totalExpenseView.setText("0.00 $");
        } else {
            setTotalExpense(0);
            if (layoutType.isLandscape()) {
                pieChartView.setOutlineColor(getResources().getColor(R.color.text_secondary));
                pieChartView.setInlineColor(getResources().getColor(R.color.text_secondary));
                totalExpenseView.setTextColor(getResources().getColor(R.color.text_secondary));
            } else {
                titleView.setVisibility(GONE);
            }
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final LayoutParams params = (LayoutParams) pieChartView.getLayoutParams();
        params.height = pieChartView.getMeasuredWidth();
    }

    public void setPieChartData(PieChartData pieChartData) {
        pieChartView.setPieChartData(pieChartData);
    }

    public void setTotalExpense(long totalExpense) {
        totalExpenseView.setText(MoneyFormatter.format(mainCurrency, totalExpense));
    }
}
