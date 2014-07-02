package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.code44.finance.R;

public class FabImageButton extends ImageButton {
    //private final Outline outline = new Outline();

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

        //mOutlineCircle.setRoundRect(0, 0, w, w, w / 2);
        //setOutline(mOutlineCircle);
    }

    private void init() {
        setBackgroundResource(R.drawable.ripple_fab);
        //setClipToOutline(true);
    }
}
