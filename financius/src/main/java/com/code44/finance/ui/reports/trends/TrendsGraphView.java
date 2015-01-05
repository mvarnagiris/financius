package com.code44.finance.ui.reports.trends;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.code44.finance.R;
import com.code44.finance.graphs.line.LineGraphData;
import com.code44.finance.graphs.line.LineGraphView;
import com.code44.finance.utils.ThemeUtils;

public class TrendsGraphView extends LinearLayout {
    private final LineGraphView trendsLineGraphView;

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
    }

    public void setLineGraphData(LineGraphData... lineGraphData) {
        trendsLineGraphView.setLineGraphData(lineGraphData);
    }
}
