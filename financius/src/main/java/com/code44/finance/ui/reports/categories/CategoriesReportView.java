package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartView;
import com.code44.finance.ui.common.ViewBackgroundTheme;
import com.code44.finance.utils.ThemeUtils;

import javax.inject.Inject;

public class CategoriesReportView extends LinearLayout {
    private final PieChartView pieChartView;
    private final TextView totalExpenseTextView;

    @Inject CurrencyFormat mainCurrencyFormat;

    public CategoriesReportView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoriesReportView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setGravity(Gravity.CENTER_VERTICAL);
        final int padding = getResources().getDimensionPixelSize(R.dimen.keyline);
        setPadding(padding, padding, padding, padding);
        inflate(context, R.layout.view_categories_report, this);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }

        // Get views
        pieChartView = (PieChartView) findViewById(R.id.pieChartView);
        totalExpenseTextView = (TextView) findViewById(R.id.totalExpenseTextView);

        // Setup
        applyStyle(context, attrs);
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
// TODO        totalExpenseTextView.setText(MoneyFormatter.format(mainCurrencyFormat, totalExpense));
    }

    private void applyStyle(Context context, AttributeSet attrs) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieChartView, 0, 0);
        try {
            final ViewBackgroundTheme viewBackgroundTheme = ViewBackgroundTheme.from(a.getInteger(R.styleable.CategoriesReportView_viewBackgroundTheme, 0));
            pieChartView.setViewBackgroundTheme(viewBackgroundTheme);
            if (getOrientation() == HORIZONTAL) {
                pieChartView.setSizeBasedOn(PieChartView.SizeBasedOn.Height);
            } else {
                MarginLayoutParams params = (MarginLayoutParams) pieChartView.getLayoutParams();
                params.leftMargin = params.rightMargin = getResources().getDimensionPixelSize(R.dimen.space_xlarge);
                pieChartView.setSizeBasedOn(PieChartView.SizeBasedOn.Width);
            }

            final int textColor = ThemeUtils.getColor(getContext(), viewBackgroundTheme == ViewBackgroundTheme.Light ? android.R.attr.textColorPrimary : android.R.attr.textColorPrimaryInverse);
            totalExpenseTextView.setTextColor(textColor);
        } finally {
            a.recycle();
        }
    }
}
