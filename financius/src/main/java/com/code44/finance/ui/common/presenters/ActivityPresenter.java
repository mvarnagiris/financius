package com.code44.finance.ui.common.presenters;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.code44.finance.ui.common.BaseActivity;

import java.util.HashSet;
import java.util.Set;

public class ActivityPresenter extends Presenter {
    private BaseActivity activity;
    private Set<ViewPresenter> registeredViewPresenters = new HashSet<>();

    public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        this.activity = activity;
    }

    public void onActivityStarted(BaseActivity activity) {
    }

    public void onActivityResumed(BaseActivity activity) {
        for (ViewPresenter viewPresenter : registeredViewPresenters) {
            viewPresenter.onActivityResume();
        }
    }

    public void onActivityPaused(BaseActivity activity) {
        for (ViewPresenter viewPresenter : registeredViewPresenters) {
            viewPresenter.onActivityPause();
        }
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

    public void registerViewPresenter(ViewPresenter viewPresenter) {
        registeredViewPresenters.add(viewPresenter);
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
