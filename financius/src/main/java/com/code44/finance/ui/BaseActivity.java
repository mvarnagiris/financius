package com.code44.finance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.utils.ToolbarHelper;

public class BaseActivity extends Activity implements NavigationFragment.NavigationListener {
    protected ToolbarHelper toolbarHelper;

    protected static Intent makeIntent(Context context, Class activityClass) {
        return new Intent(context, activityClass);
    }

    protected static void startScaleUp(Context context, Intent intent, View expandFromView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && expandFromView != null && context instanceof Activity) {
            final Bundle options = ActivityOptionsCompat
                    .makeScaleUpAnimation(expandFromView, 0, 0, expandFromView.getWidth(), expandFromView.getHeight())
                    .toBundle();
            ActivityCompat.startActivity((Activity) context, intent, options);
        } else {
            context.startActivity(intent);
        }
    }

    protected static void startForResult(Fragment fragment, Intent intent, int requestCode) {
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Init
        toolbarHelper = new ToolbarHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toolbarHelper.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.action_settings:
                SettingsActivity.start(this, null);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationItemSelected(NavigationAdapter.NavigationItem item) {

    }
}
