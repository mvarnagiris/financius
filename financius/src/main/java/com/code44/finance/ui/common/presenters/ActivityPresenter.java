package com.code44.finance.ui.common.presenters;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.code44.finance.ui.common.BaseActivity;

public class ActivityPresenter extends Presenter {
    private BaseActivity activity;

    public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        this.activity = activity;
    }

    public void onActivityStarted(BaseActivity activity) {
    }

    public void onActivityResumed(BaseActivity activity) {
    }

    public void onActivityPaused(BaseActivity activity) {
    }

    public void onActivityStopped(BaseActivity activity) {
    }

    public void onActivitySaveInstanceState(BaseActivity activity, Bundle outState) {
    }

    public void onActivityDestroyed(BaseActivity activity) {
        this.activity = null;
    }

    public boolean onActivityCreateOptionsMenu(BaseActivity activity, Menu menu) {
        return false;
    }

    public boolean onActivityOptionsItemSelected(BaseActivity activity, MenuItem item) {
        return false;
    }

    public BaseActivity getActivity() {
        return activity;
    }

    protected <T extends View> T findView(BaseActivity activity, @IdRes int viewId) {
        //noinspection unchecked
        T view = (T) activity.findViewById(viewId);
        if (view == null) {
            throw new IllegalStateException("Layout must contain view with id: " + activity.getResources().getResourceName(viewId) + " in " + activity.getClass().getName() + ". You should override onViewCreated(Bundle) method.");
        }

        return view;
    }
}
