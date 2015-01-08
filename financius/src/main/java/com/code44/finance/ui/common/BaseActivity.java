package com.code44.finance.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.ui.common.presenters.ActivityPresenter;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.ui.settings.security.UnlockActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.utils.errors.AppError;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public abstract class BaseActivity extends ActionBarActivity {
    private static final int REQUEST_UNLOCK = 17172;

    private final Object eventHandler = new Object() {
        @Subscribe public void onAppError(AppError error) {
            onHandleError(error);
        }
    };
    private final Object killEventHandler = new Object() {
        @Subscribe public void onKill(KillEverythingThanMoves kill) {
            kill();
        }
    };

    @Inject Security security;
    @Inject EventBus eventBus;
    @Inject Analytics analytics;

    private ActivityPresenter activityPresenter;
    private boolean isKilling = false;

    protected static Intent makeIntentForActivity(Context context, Class activityClass) {
        return new Intent(context, activityClass);
    }

    protected static void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

    protected static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateView(savedInstanceState);
        App.with(this).inject(this);
        eventBus.register(killEventHandler);

        activityPresenter = onCreateActivityPresenter();
        if (activityPresenter != null) {
            activityPresenter.onActivityCreated(this, savedInstanceState);
        }
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UNLOCK && resultCode != RESULT_OK) {
            kill();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onStart() {
        super.onStart();
        if (activityPresenter != null) {
            activityPresenter.onActivityStarted(this);
        }
    }

    @Override protected void onResume() {
        super.onResume();

        getEventBus().register(eventHandler);

        final Analytics.Screen screen = getScreen();
        if (screen == Analytics.Screen.None) {
            getAnalytics().clearScreen();
        } else {
            getAnalytics().trackScreen(screen);
        }

        if (security.isUnlockRequired()) {
            UnlockActivity.startForResult(this, REQUEST_UNLOCK, true);
        } else {
            security.setLastUnlockTimestamp(System.currentTimeMillis());
        }

        if (activityPresenter != null) {
            activityPresenter.onActivityResumed(this);
        }
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(eventHandler);
        getAnalytics().clearScreen();

        if (!isKilling) {
            security.setLastUnlockTimestamp(System.currentTimeMillis());
        }

        if (activityPresenter != null) {
            activityPresenter.onActivityPaused(this);
        }
    }

    @Override protected void onStop() {
        super.onStop();

        if (activityPresenter != null) {
            activityPresenter.onActivityStopped(this);
        }
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (activityPresenter != null) {
            activityPresenter.onActivitySaveInstanceState(this, outState);
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(killEventHandler);

        if (activityPresenter != null) {
            activityPresenter.onActivityDestroyed(this);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.common, menu);

        if (activityPresenter != null) {
            activityPresenter.onActivityCreateOptionsMenu(this, menu);
        }

        return true;
    }

    @Override public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (activityPresenter != null) {
            activityPresenter.onActivityPrepareOptionsMenu(this, menu);
        }

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return activityPresenter.onActivityOptionsItemSelected(this, item) || super.onOptionsItemSelected(item);
    }

    protected void onCreateView(Bundle savedInstanceState) {
    }

    protected void onHandleError(AppError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    protected Security getSecurity() {
        return security;
    }

    protected EventBus getEventBus() {
        return eventBus;
    }

    protected Analytics getAnalytics() {
        return analytics;
    }

    protected Analytics.Screen getScreen() {
        return Analytics.Screen.None;
    }

    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    protected void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    protected ActivityPresenter onCreateActivityPresenter() {
        return null;
    }

    private void kill() {
        isKilling = true;
        finish();
    }

    public static class KillEverythingThanMoves {
    }
}