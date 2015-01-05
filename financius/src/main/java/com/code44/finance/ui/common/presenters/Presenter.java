package com.code44.finance.ui.common.presenters;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;

public abstract class Presenter {
    private boolean isResumed = false;

    public void onResume() {
        isResumed = true;
    }

    public void onPause() {
        isResumed = false;
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void showError(Throwable error) {
    }

    protected boolean isResumed() {
        return isResumed;
    }

    protected <T extends View> T findView(Activity activity, @IdRes int viewId) {
        return findView(activity.findViewById(android.R.id.content), viewId);
    }

    protected <T extends View> T findView(View parent, @IdRes int viewId) {
        //noinspection unchecked
        T view = (T) parent.findViewById(viewId);
        if (view == null) {
            throw new IllegalStateException("Layout must contain view with id: " + parent.getContext().getResources().getResourceName(viewId));
        }

        return view;
    }
}
