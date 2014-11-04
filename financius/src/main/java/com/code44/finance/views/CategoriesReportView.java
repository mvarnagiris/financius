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
    @Inject @Main Currency mainCurrency;

    private PieChartView pieChartView;
    private TextView totalExpenseTextView;

    public CategoriesReportView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoriesReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        // Get views
        pieChartView = (PieChartView) findViewById(R.id.pieChartView);
        totalExpenseTextView = (TextView) findViewById(R.id.totalExpenseTextView);

        // Setup
        pieChartView.setEmptyColor(totalExpenseTextView.getCurrentTextColor());
        setPieChartData(null);
        if (isInEditMode()) {
            totalExpenseTextView.setText("0.00 $");
        } else {
            setTotalExpense(0);
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
        totalExpenseTextView.setText(MoneyFormatter.format(mainCurrency, totalExpense));
    }
}
