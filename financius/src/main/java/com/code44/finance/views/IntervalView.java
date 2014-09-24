package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.utils.ActiveInterval;

import javax.inject.Inject;

public class IntervalView extends LinearLayout implements View.OnClickListener {
    private final ImageButton prev_IB;
    private final ImageButton next_IB;
    private final TextView interval_TV;

    @Inject ActiveInterval activeInterval;

    public IntervalView(Context context) {
        this(context, null);
    }

    public IntervalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntervalView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public IntervalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // Get views
        prev_IB = (ImageButton) findViewById(R.id.prev_IB);
        next_IB = (ImageButton) findViewById(R.id.next_IB);
        interval_TV = (TextView) findViewById(R.id.interval_TV);

        // Setup
        prev_IB.setOnClickListener(this);
        next_IB.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prev_IB:
                prev();
                break;
            case R.id.next_IB:
                next();
                break;
        }
    }

    private void prev() {
        // TODO Implement
    }

    private void next() {
        // TODO Implement
    }
}
