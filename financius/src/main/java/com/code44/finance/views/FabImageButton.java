package com.code44.finance.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.code44.finance.R;

public class FabImageButton extends ImageButton {
    public FabImageButton(Context context) {
        super(context);
        init();
    }

    public FabImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FabImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundResource(R.drawable.btn_fab);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).setStartDelay(0).start();
                break;

            case MotionEvent.ACTION_UP:
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).setStartDelay(0).start();
                break;
        }
        return super.onTouchEvent(event);
    }
}
