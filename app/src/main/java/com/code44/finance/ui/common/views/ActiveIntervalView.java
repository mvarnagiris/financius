package com.code44.finance.ui.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.interval.ActiveInterval;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ActiveIntervalView extends LinearLayout implements View.OnClickListener {
    private final Button intervalButton;

    @Inject ActiveInterval activeInterval;
    @Inject EventBus eventBus;

    public ActiveIntervalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveIntervalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_active_interval, this);
        if (!isInEditMode()) {
            App.with(context).inject(this);
        }

        // Get views
        final ImageButton previousImageButton = (ImageButton) findViewById(R.id.previousImageButton);
        final ImageButton nextImageButton = (ImageButton) findViewById(R.id.nextImageButton);
        intervalButton = (Button) findViewById(R.id.intervalButton);

        // Setup
        previousImageButton.setOnClickListener(this);
        nextImageButton.setOnClickListener(this);
        intervalButton.setOnClickListener(this);
        if (!isInEditMode()) {
            eventBus.register(this);
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previousImageButton:
                previous();
                break;
            case R.id.nextImageButton:
                next();
                break;
            case R.id.intervalButton:
                reset();
                break;
        }
    }

    @Subscribe public void onActiveIntervalChanged(ActiveInterval activeInterval) {
        update(activeInterval);
    }

    private void update(ActiveInterval activeInterval) {
        intervalButton.setText(activeInterval.getTitle());
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
