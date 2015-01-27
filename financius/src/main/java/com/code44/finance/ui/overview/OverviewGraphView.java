package com.code44.finance.ui.overview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.graphs.pie.PieChartView;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.ui.common.ViewBackgroundTheme;
import com.code44.finance.utils.LayoutType;
import com.code44.finance.utils.ThemeUtils;

import javax.inject.Inject;

public class OverviewGraphView extends LinearLayout {
    private final PieChartView pieChartView;
    private final TextView totalExpenseTextView;

    @Inject AmountFormatter amountFormatter;
    @Inject LayoutType layoutType;

    public OverviewGraphView(Context context) {
        this(context, null);
    }

    public OverviewGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverviewGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(ThemeUtils.getDrawable(context, R.attr.selectableItemBackgroundBorderless));
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(VERTICAL);
        final int padding = getResources().getDimensionPixelSize(R.dimen.keyline);
        setPadding(padding, padding, padding, padding);
        inflate(context, R.layout.view_overview_graph, this);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }

        // Get views
        final TextView titleView = (TextView) findViewById(R.id.titleTextView);
        pieChartView = (PieChartView) findViewById(R.id.pieChartView);
        totalExpenseTextView = (TextView) findViewById(R.id.totalExpenseTextView);

        // Setup
        applyStyle(context, attrs);
        pieChartView.setEmptyColor(totalExpenseTextView.getCurrentTextColor());
        setPieChartData(null);
        if (isInEditMode()) {
            totalExpenseTextView.setText("0.00 $");
        } else {
            setTotalExpense(0);
            if (!layoutType.isLandscape()) {
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
        totalExpenseTextView.setText(amountFormatter.format(totalExpense));
    }

    private void applyStyle(Context context, AttributeSet attrs) {
        final TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieChartView, 0, 0);
        try {
            final ViewBackgroundTheme viewBackgroundTheme = ViewBackgroundTheme.from(a.getInteger(R.styleable.OverviewGraphView_viewBackgroundTheme, 0));
            pieChartView.setViewBackgroundTheme(viewBackgroundTheme);

            final int textColor = ThemeUtils.getColor(getContext(), viewBackgroundTheme == ViewBackgroundTheme.Light ? android.R.attr.textColorPrimary : android.R.attr.textColorPrimaryInverse);
            totalExpenseTextView.setTextColor(textColor);
        } finally {
            a.recycle();
        }
    }
}
