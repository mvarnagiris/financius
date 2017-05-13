package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.money.AmountFormatter;

import javax.inject.Inject;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class CategoriesPieChartView extends LinearLayout {
    private final PieChartView pieChartView;
    private final TextView totalExpenseTextView;

    @Inject AmountFormatter amountFormatter;

    public CategoriesPieChartView(Context context) {
        this(context, null);
    }

    public CategoriesPieChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoriesPieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_categories_pie_chart, this);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }

        // Get views
        pieChartView = (PieChartView) findViewById(R.id.pieChartView);
        totalExpenseTextView = (TextView) findViewById(R.id.totalExpenseTextView);

        // Setup
        pieChartView.setValueSelectionEnabled(false);
        pieChartView.setChartRotation(-90, false);
        pieChartView.setChartRotationEnabled(false);
        setGravity(Gravity.CENTER_VERTICAL);
        final int padding = getResources().getDimensionPixelSize(R.dimen.keyline);
        setPadding(padding, 0, padding, 0);
        if (isInEditMode()) {
            totalExpenseTextView.setText("0.00 $");
        }
    }

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final ViewGroup squareParent = (ViewGroup) pieChartView.getParent();
        final int squareSize = squareParent.getMeasuredHeight();
        if (pieChartView.getMeasuredWidth() != squareSize) {
            pieChartView.getLayoutParams().width = squareSize;
            squareParent.getChildAt(0).getLayoutParams().width = squareSize;
        }
    }

    public void setPieChartData(PieChartData pieChartData, String currencyCode) {
        if (pieChartData != null) {
            float total = 0;
            for (SliceValue value : pieChartData.getValues()) {
                total += value.getValue();
            }
            setTotal((long) total, currencyCode);

            pieChartData.setHasLabels(false);
            pieChartData.setHasLabelsOutside(false);
            pieChartData.setHasLabelsOnlyForSelected(false);
        } else {
            setTotal(0, currencyCode);
        }

        pieChartView.setPieChartData(pieChartData);
    }

    private void setTotal(long totalExpense, String currencyCode) {
        totalExpenseTextView.setText(amountFormatter.format(currencyCode, totalExpense));
    }
}
