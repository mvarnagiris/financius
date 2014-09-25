package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.utils.ActiveInterval;
import com.code44.finance.utils.EventBus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ActiveIntervalView extends LinearLayout implements View.OnClickListener {
    private final Button interval_B;

    @Inject ActiveInterval activeInterval;
    @Inject EventBus eventBus;

    @SuppressWarnings("UnusedDeclaration") public ActiveIntervalView(Context context) {
        this(context, null);
    }

    public ActiveIntervalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveIntervalView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActiveIntervalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.v_interval, this);
        App.with(context).inject(this);

        // Get views
        final ImageButton prev_IB = (ImageButton) findViewById(R.id.prev_IB);
        final ImageButton next_IB = (ImageButton) findViewById(R.id.next_IB);
        interval_B = (Button) findViewById(R.id.interval_B);

        // Setup
        prev_IB.setOnClickListener(this);
        next_IB.setOnClickListener(this);
        interval_B.setOnClickListener(this);
        eventBus.register(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.prev_IB:
                previous();
                break;
            case R.id.next_IB:
                next();
                break;
            case R.id.interval_B:
                reset();
                break;
        }
    }

    @Subscribe public void onActiveIntervalChanged(ActiveInterval activeInterval) {
        update(activeInterval);
    }

    private void update(ActiveInterval activeInterval) {
        interval_B.setText(activeInterval.getTitle());
    }

    private void previous() {
        activeInterval.previous();
    }

    private void next() {
        activeInterval.next();
    }

    private void reset() {
        activeInterval.reset();
    }
}
