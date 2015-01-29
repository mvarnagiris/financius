package com.code44.finance.ui.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.code44.finance.R;

public class TintImageButton extends ImageButton {
    public TintImageButton(Context context) {
        super(context);
        init(context, null);
    }

    public TintImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TintImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int tintColor = 0;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintImageButton);

            tintColor = a.getColor(R.styleable.TintImageButton_supportTintColor, 0);

            a.recycle();
        }

        if (tintColor != 0) {
            setColorFilter(tintColor);
        }
    }
}
