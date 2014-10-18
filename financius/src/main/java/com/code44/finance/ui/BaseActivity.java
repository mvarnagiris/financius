package com.code44.finance.ui;

import android.app.Fragment;
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
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.analytics.Analytics;
import com.code44.finance.utils.errors.AppError;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class BaseActivity extends ActionBarActivity {
    private final Object eventHandler = new Object() {
        @Subscribe public void onAppError(AppError error) {
            onHandleError(error);
        }
    };

    @Inject EventBus eventBus;
    @Inject Analytics analytics;

    protected static Intent makeIntent(Context context, Class activityClass) {
        return new Intent(context, activityClass);
    }

    protected static void start(Context context, Intent intent) {
        context.startActivity(intent);
    }

    protected static void startForResult(Fragment fragment, Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.with(this).inject(this);
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
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
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(eventHandler);
        getAnalytics().clearScreen();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                SettingsActivity.start(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onHandleError(AppError error) {
        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
}