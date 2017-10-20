package com.code44.finance.ui.settings.security;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.ui.settings.security.pattern.AppearAnimationCreator;
import com.code44.finance.ui.settings.security.pattern.AppearAnimationUtils;
import com.code44.finance.ui.settings.security.pattern.LockPatternView;

import java.util.List;

public class PatternLockView extends LockView implements
        AppearAnimationCreator<LockPatternView.CellState> {
    private static final boolean SUPPORTS_LOLLIPOP = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    // how long before we clear the wrong pattern
    private static final int PATTERN_CLEAR_TIMEOUT_MS = 2000;

    private final TextView mHelpMessage;
    private final LockPatternView mLockPatternView;
    private final AppearAnimationUtils mAppearAnimationUtils;

    /**
     * Useful for clearing out the wrong pattern after a delay
     */
    private Runnable mCancelPatternRunnable = new Runnable() {
        public void run() {
            mLockPatternView.clearPattern();
        }
    };

    public PatternLockView(Context context) {
        this(context, null);
    }

    public PatternLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_pattern_lock, this);

        // Get views
        mLockPatternView = (LockPatternView) findViewById(R.id.patternLock);
        mHelpMessage = (TextView) findViewById(R.id.patternLockMessage);

        // Setup
        mLockPatternView.setSaveEnabled(false);
        mLockPatternView.setFocusable(false);
        mLockPatternView.setOnPatternListener(new UnlockPatternListener());

        mAppearAnimationUtils = new AppearAnimationUtils(context,
                AppearAnimationUtils.DEFAULT_APPEAR_DURATION, 1.5f /* delayScale */,
                2.0f /* transitionScale */, AnimationUtils.loadInterpolator(
                context, SUPPORTS_LOLLIPOP
                        ? android.R.interpolator.linear_out_slow_in
                        : android.R.interpolator.decelerate_quad));
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAppearAnimation();
    }

    @Override public void showError(String message) {
        mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
        mLockPatternView.postDelayed(mCancelPatternRunnable, PATTERN_CLEAR_TIMEOUT_MS);
    }

    @Override public void onShowNewLock() {
        mLockPatternView.removeCallbacks(mCancelPatternRunnable);
        mLockPatternView.clearPattern();
    }

    @Override public void onShowNewLockConfirm() {
        mLockPatternView.removeCallbacks(mCancelPatternRunnable);
        mLockPatternView.clearPattern();
    }

    @Override public void onShowUnlock() {
        mLockPatternView.removeCallbacks(mCancelPatternRunnable);
        mLockPatternView.clearPattern();
    }

    private void startAppearAnimation() {
        setClipChildren(false);

        setAlpha(1f);
        setTranslationY(mAppearAnimationUtils.getStartTranslation());
        animate()
                .setDuration(500)
                .setInterpolator(mAppearAnimationUtils.getInterpolator())
                .translationY(0);
        mAppearAnimationUtils.startAppearAnimation(
                mLockPatternView.getCellStates(),
                new Runnable() {
                    @Override
                    public void run() {
                        setClipChildren(true);
                    }
                },
                this);
        if (!TextUtils.isEmpty(mHelpMessage.getText())) {
            mAppearAnimationUtils.createAnimation(mHelpMessage, 0,
                    AppearAnimationUtils.DEFAULT_APPEAR_DURATION,
                    mAppearAnimationUtils.getStartTranslation(),
                    mAppearAnimationUtils.getInterpolator(),
                    null /* finishRunnable */);
        }
    }

    @Override public void createAnimation(final LockPatternView.CellState animatedCell,
                                          long delay, long duration, float startTranslationY,
                                          Interpolator interpolator, final Runnable finishListener) {
        animatedCell.scale = 0.0f;
        animatedCell.translateY = startTranslationY;
        ValueAnimator animator = ValueAnimator.ofFloat(startTranslationY, 0.0f);
        animator.setInterpolator(interpolator);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedCell.scale = animation.getAnimatedFraction();
                animatedCell.translateY = (float) animation.getAnimatedValue();
                mLockPatternView.invalidate();
            }
        });
        if (finishListener != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    finishListener.run();
                }
            });
        }
        animator.start();
        mLockPatternView.invalidate();
    }

    private class UnlockPatternListener implements LockPatternView.OnPatternListener {

        public void onPatternStart() {
            mLockPatternView.removeCallbacks(mCancelPatternRunnable);
        }

        public void onPatternCleared() {
        }

        public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
        }

        public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            String password = LockPatternView.patternToString(pattern);
            onPasswordEntered(password);
        }
    }
}
