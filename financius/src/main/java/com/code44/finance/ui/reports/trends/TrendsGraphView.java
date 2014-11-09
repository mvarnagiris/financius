package com.code44.finance.ui.reports.trends;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.code44.finance.R;

public class TrendsGraphView extends LinearLayout {
    public TrendsGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendsGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        final int padding = getResources().getDimensionPixelSize(R.dimen.keyline);
        setPadding(padding, padding, padding, padding);
        inflate(context, R.layout.view_trends_graph, this);
    }
}
