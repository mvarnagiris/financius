package com.code44.finance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.utils.AppError;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.IntervalHelper;
import com.code44.finance.utils.ToolbarHelper;
import com.squareup.otto.Subscribe;

public class BaseActivity extends Activity {
    protected final EventBus eventBus = EventBus.get();
    protected final IntervalHelper intervalHelper = IntervalHelper.get();
    protected final GeneralPrefs generalPrefs = GeneralPrefs.get();

    private final Object eventHandler = new Object() {
        @Subscribe public void onAppError(AppError error) {
            onHandleError(error);
        }
    };

    protected ToolbarHelper toolbarHelper;

    protected static Intent makeIntent(Context context, Class activityClass) {
        return new Intent(context, activityClass);
    }

    protected static void start(Context context, Intent intent) {
        context.startActivity(intent);
    }

    protected static void startForResult(Fragment fragment, Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override protected void onResume() {
        super.onResume();
        getEventBus().register(eventHandler);
    }

    @Override protected void onPause() {
        super.onPause();
        getEventBus().unregister(eventHandler);
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Init
        toolbarHelper = new ToolbarHelper(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (toolbarHelper.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
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

    protected IntervalHelper getIntervalHelper() {
        return intervalHelper;
    }

    protected GeneralPrefs getGeneralPrefs() {
        return generalPrefs;
    }
}