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
import com.code44.finance.utils.MoneyFormatter;

import javax.inject.Inject;

public class CategoriesReportView extends LinearLayout {
    private final PieChartView pieChart_V;
    private final TextView totalExpense_TV;

    @Inject @Main Currency mainCurrency;

    @SuppressWarnings("UnusedDeclaration")
    public CategoriesReportView(Context context) {
        this(context, null);
    }

    public CategoriesReportView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoriesReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        App.with(context).inject(this);
        inflate(context, R.layout.v_categories_report, this);
        setBackgroundResource(R.drawable.btn_borderless);

        // Get views
        pieChart_V = (PieChartView) findViewById(R.id.pieChart);
        totalExpense_TV = (TextView) findViewById(R.id.totalExpense);

        // Setup
        pieChart_V.setEmptyColor(totalExpense_TV.getCurrentTextColor());
        setPieChartData(null);
        if (isInEditMode()) {
            totalExpense_TV.setText("0.00 $");
        } else {
            setTotalExpense(0);
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final LayoutParams params = (LayoutParams) pieChart_V.getLayoutParams();
        params.height = pieChart_V.getMeasuredWidth();
    }

    public void setPieChartData(PieChartData pieChartData) {
        pieChart_V.setPieChartData(pieChartData);
    }

    public void setTotalExpense(long totalExpense) {
        totalExpense_TV.setText(MoneyFormatter.format(mainCurrency, totalExpense));
    }
}
