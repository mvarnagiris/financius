package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.code44.finance.R;

public class FabImageButton extends ImageButton {
    private final android.graphics.Outline outline = new android.graphics.Outline();

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        outline.setRoundRect(0, 0, w, w, w / 2);
        setOutline(outline);
    }

    private void init() {
        setBackgroundResource(R.drawable.ripple_fab);
        setClipToOutline(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start();
                break;

            case MotionEvent.ACTION_UP:
                animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start();
                break;
        }
        return super.onTouchEvent(event);
    }
}
