package com.code44.finance.ui.common;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;

public abstract class ViewController {
    protected abstract void showError(Throwable error);

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
