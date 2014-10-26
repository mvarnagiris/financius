package com.code44.finance.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.overview.OverviewActivity;

public abstract class DrawerActivity extends BaseActivity implements NavigationFragment.NavigationListener {
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_drawer);
    }

    @Override public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, (ViewGroup) findViewById(R.id.content), true);

        // Get views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Setup
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.open_navigation, R.string.close_navigation);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START) || drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override public void onNavigationItemSelected(NavigationAdapter.NavigationItem item) {
        switch (item.getId()) {
            default:
                OverviewActivity.start(this);
                finish();
                break;
        }
    }
}
