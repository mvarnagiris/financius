package com.code44.finance.ui.common.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.ui.accounts.list.AccountsActivity;
import com.code44.finance.ui.common.navigation.NavigationFragment;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.overview.OverviewActivity;
import com.code44.finance.ui.reports.categories.CategoriesReportActivity;
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.ui.transactions.list.TransactionsActivity;

public abstract class BaseDrawerActivity extends BaseActivity implements NavigationFragment.NavigationListener {
    private static final int DRAWER_LAUNCH_DELAY = 250;

    private final Handler handler = new Handler();

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;

    private NavigationFragment navigationFragment;
    private boolean showDrawer = false;
    private boolean showDrawerToggle = false;

    @Override protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        super.setContentView(R.layout.activity_drawer);
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof NavigationFragment) {
            navigationFragment = (NavigationFragment) fragment;
            navigationFragment.setSelected(getNavigationScreen());
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (navigationFragment != null) {
            navigationFragment.setSelected(getNavigationScreen());
        }
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override public void onBackPressed() {
        if (drawerLayout != null && (drawerLayout.isDrawerOpen(Gravity.START) || drawerLayout.isDrawerOpen(Gravity.END))) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerToggle != null && drawerLayout != null) {
                    if (drawerLayout.isDrawerOpen(Gravity.START)) {
                        drawerLayout.closeDrawer(Gravity.START);
                    } else {
                        drawerLayout.openDrawer(Gravity.START);
                    }
                    return true;
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void setContentView(int layoutResID) {
        LayoutInflater.from(this).inflate(layoutResID, (ViewGroup) findViewById(R.id.content), true);

        // Get views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Setup
        if (!showDrawer) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            drawerLayout = null;
        }

        if (drawerLayout != null && showDrawerToggle) {
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.open_navigation, R.string.close_navigation) {
                @Override public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, 0);
                }
            };
            drawerLayout.setDrawerListener(drawerToggle);
        }
        setupToolbar();
    }

    @Override public void onNavigationItemSelected(NavigationScreen navigationScreen) {
        if (isSameNavigationScreen(navigationScreen)) {
            drawerLayout.closeDrawers();
            return;
        }

        final Intent intent;
        switch (navigationScreen) {
            case User:
                intent = null;
                break;
            case Overview:
                intent = OverviewActivity.makeIntent(this);
                break;
            case Accounts:
                intent = AccountsActivity.makeViewIntent(this);
                break;
            case Transactions:
                intent = TransactionsActivity.makeViewIntent(this);
                break;
            case Reports:
                intent = CategoriesReportActivity.makeIntent(this);
                break;
            case Settings:
                intent = SettingsActivity.makeIntent(this);
                break;
            default:
                intent = null;
                break;
        }

        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }

        handler.postDelayed(new Runnable() {
            @Override public void run() {
                startActivity(BaseDrawerActivity.this, intent);
                if (!(BaseDrawerActivity.this instanceof OverviewActivity)) {
                    finish();
                }
            }
        }, DRAWER_LAUNCH_DELAY);
    }

    protected NavigationScreen getNavigationScreen() {
        return null;
    }

    protected void setShowDrawer(boolean showDrawer) {
        this.showDrawer = showDrawer;
    }

    protected void setShowDrawerToggle(boolean showDrawerToggle) {
        this.showDrawerToggle = showDrawerToggle;
    }

    private boolean isSameNavigationScreen(NavigationScreen navigationScreen) {
        switch (navigationScreen) {
            case User:
                break;
            case Overview:
                return this instanceof OverviewActivity;
            case Accounts:
                return this instanceof AccountsActivity;
            case Transactions:
                return this instanceof TransactionsActivity;
            case Reports:
                return this instanceof CategoriesReportActivity;
        }
        return false;
    }
}
