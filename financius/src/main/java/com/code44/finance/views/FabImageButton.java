package com.code44.finance.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;

import com.code44.finance.R;

public class FabImageButton extends ImageButton {
    private static final boolean SUPPORTS_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    private float normalElevation;
    private float pressedElevation;

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) private void init() {
        setBackgroundResource(R.drawable.btn_fab);
        normalElevation = getResources().getDimension(R.dimen.elevation_fab);
        pressedElevation = normalElevation * 3;
        if (SUPPORTS_LOLLIPOP) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (SUPPORTS_LOLLIPOP) {
                    animate().translationZ(pressedElevation).setDuration(150).setStartDelay(0).start();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (SUPPORTS_LOLLIPOP) {
                    animate().translationZ(normalElevation).setDuration(150).setStartDelay(0).start();
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
