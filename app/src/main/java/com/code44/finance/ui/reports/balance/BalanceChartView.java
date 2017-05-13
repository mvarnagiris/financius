package com.code44.finance.ui.reports.balance;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.code44.finance.R;
import com.code44.finance.utils.ThemeUtils;

import java.util.Collections;
import java.util.Comparator;

import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class BalanceChartView extends LinearLayout {
    private final LineChartView lineChartView;

    public BalanceChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BalanceChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        final int padding = getResources().getDimensionPixelSize(R.dimen.keyline);
        setPadding(padding, padding, padding, padding);
        setBackground(ThemeUtils.getDrawable(context, R.attr.selectableItemBackgroundBorderless));
        inflate(context, R.layout.view_balance_graph, this);

        // Get views
        lineChartView = (LineChartView) findViewById(R.id.lineChartView);
        lineChartView.setZoomEnabled(false);
        lineChartView.setValueSelectionEnabled(true);
    }

    public void setLineGraphData(LineChartData lineChartData) {
        lineChartView.setLineChartData(lineChartData);

        final PointValue maxValue = Collections.max(lineChartData.getLines().get(0).getValues(), new Comparator<PointValue>() {
            @Override public int compare(PointValue lhs, PointValue rhs) {
                return Float.compare(lhs.getY(), rhs.getY());
            }
        });

        final Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.inset(-0.2f, -maxValue.getY() * 0.08f);
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewportWithAnimation(viewport);
    }
}
