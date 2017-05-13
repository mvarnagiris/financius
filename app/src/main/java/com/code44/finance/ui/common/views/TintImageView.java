package com.code44.finance.ui.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.code44.finance.R;

public class TintImageView extends ImageView {
    public TintImageView(Context context) {
        super(context);
        init(context, null);
    }

    public TintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TintImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int tintColor = 0;

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TintImageView);

            tintColor = a.getColor(R.styleable.TintImageView_supportTintColor, 0);

            a.recycle();
        }

        if (tintColor != 0) {
            setColorFilter(tintColor);
        }
    }
}
