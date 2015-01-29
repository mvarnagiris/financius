package com.code44.finance.ui.common.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.code44.finance.R;

public class EmptyContainerView extends FrameLayout implements View.OnClickListener {
    private final ImageButton addImageButton;

    private Callbacks callbacks;
    private boolean isEmpty = false;

    public EmptyContainerView(Context context) {
        this(context, null);
    }

    public EmptyContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_empty_container, this);

        // Get views
        addImageButton = (ImageButton) findViewById(R.id.addImageButton);

        // Setup
        addImageButton.setOnClickListener(this);
        update();
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addImageButton:
                onAddClick();
                break;
        }
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
        update();
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    private void update() {
        if (isEmpty) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    private void onAddClick() {
        if (callbacks != null) {
            callbacks.onEmptyAddClick(addImageButton);
        }
    }

    public static interface Callbacks {
        public void onEmptyAddClick(View v);
    }
}
