package com.code44.finance.ui.common.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.ui.settings.security.Security;
import com.code44.finance.ui.settings.security.UnlockActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.errors.AppError;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends ActionBarActivity {
    private static final int REQUEST_UNLOCK = 17172;

    private final Object eventHandler = new Object() {
        @Subscribe public void onAppError(AppError error) {
            showError(error);
        }
    };

    @Inject Security security;
    @Inject EventBus eventBus;
    @Inject Analytics analytics;

    private boolean isKilling = false;
    private final Object killEventHandler = new Object() {
        @Subscribe public void onKill(KillEverythingThanMoves kill) {
            kill();
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.with(this).inject(this);
        eventBus.register(killEventHandler);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UNLOCK && resultCode != RESULT_OK) {
            kill();
        }
    }

    @Override protected void onResume() {
        super.onResume();

        getEventBus().register(eventHandler);

        final Screens.Screen screen = getScreen();
        if (screen == Screens.Screen.None) {
            getAnalytics().screen().clearScreen();
        } else {
            getAnalytics().screen().trackScreen(screen);
        }

        if (security.isUnlockRequired()) {
            UnlockActivity.startForResult(this, REQUEST_UNLOCK, true);
        } else {
            security.setLastUnlockTimestamp(System.currentTimeMillis());
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(eventHandler);
        getAnalytics().screen().clearScreen();

        if (!isKilling) {
            security.setLastUnlockTimestamp(System.currentTimeMillis());
        }
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        eventBus.unregister(killEventHandler);
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @NonNull protected Security getSecurity() {
        return security;
    }

    @NonNull protected EventBus getEventBus() {
        return eventBus;
    }

    @NonNull protected Analytics getAnalytics() {
        return analytics;
    }

    @NonNull protected Screens.Screen getScreen() {
        return Screens.Screen.None;
    }

    @Nullable protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    protected void showError(@NonNull Throwable error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    protected void setupToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    private void kill() {
        isKilling = true;
        finish();
    }

    public static class KillEverythingThanMoves {
    }
}