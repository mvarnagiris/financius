package com.code44.finance.ui.reports.trends;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.graphs.line.LineGraphData;
import com.code44.finance.graphs.line.LineGraphView;
import com.code44.finance.utils.MoneyFormatter;
import com.code44.finance.utils.ThemeUtils;

public class TrendsGraphView extends LinearLayout {
    private final LineGraphView trendsLineGraphView;
    private final TextView maxExpenseTextView;
    private final TextView totalIncomeTextView;
    private final TextView totalExpenseTextView;

    public TrendsGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendsGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        final int padding = getResources().getDimensionPixelSize(R.dimen.keyline);
        setPadding(padding, padding, padding, padding);
        setBackground(ThemeUtils.getDrawable(context, R.attr.selectableItemBackgroundBorderless));
        inflate(context, R.layout.view_trends_graph, this);

        // Get views
        trendsLineGraphView = (LineGraphView) findViewById(R.id.trendsLineGraphView);
        maxExpenseTextView = (TextView) findViewById(R.id.maxExpenseTextView);
        totalIncomeTextView = (TextView) findViewById(R.id.totalIncomeTextView);
        totalExpenseTextView = (TextView) findViewById(R.id.totalExpenseTextView);

        if (isInEditMode()) {
            maxExpenseTextView.setText("128.04 $");
            totalIncomeTextView.setText("5892.54 $");
            totalExpenseTextView.setText("2477.00 $");
        }
    }

    public void setTotalIncomeAndExpense(long totalIncome, long totalExpense, Currency currency) {
        totalIncomeTextView.setText(MoneyFormatter.format(currency, totalIncome));
        totalExpenseTextView.setText(MoneyFormatter.format(currency, totalExpense));
    }

    public void setLineGraphData(LineGraphData lineGraphData, Currency currency) {
        trendsLineGraphView.setLineGraphData(lineGraphData);
        maxExpenseTextView.setText(MoneyFormatter.format(currency, (long) lineGraphData.getMaxValue()));
    }
}
