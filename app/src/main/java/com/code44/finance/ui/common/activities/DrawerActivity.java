package com.code44.finance.ui.common.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.ui.accounts.list.AccountsActivity;
import com.code44.finance.ui.common.navigation.NavigationFragment;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.ui.overview.OverviewActivity;
import com.code44.finance.ui.reports.categories.CategoriesReportActivity;
import com.code44.finance.ui.settings.SettingsActivity;
import com.code44.finance.ui.transactions.list.TransactionsActivity;
import com.code44.finance.ui.user.BaseLoginActivity;
import com.code44.finance.ui.user.ProfileActivity;

import javax.inject.Inject;

public abstract class DrawerActivity extends BaseActivity implements NavigationFragment.NavigationListener {
    static final String EXTRA_SHOW_DRAWER = DrawerActivity.class.getName() + ".EXTRA_SHOW_DRAWER";
    static final String EXTRA_SHOW_DRAWER_TOGGLE = DrawerActivity.class.getName() + ".EXTRA_SHOW_DRAWER_TOGGLE";

    private static final int DRAWER_LAUNCH_DELAY = 250;

    private final Handler handler = new Handler();

    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;

    @Inject User user;

    private NavigationFragment navigationFragment;
    private boolean showDrawer;
    private boolean showDrawerToggle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_drawer);

        // Get extras
        showDrawer = getIntent().getBooleanExtra(EXTRA_SHOW_DRAWER, false);
        showDrawerToggle = getIntent().getBooleanExtra(EXTRA_SHOW_DRAWER_TOGGLE, false);
    }

    @Override public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof NavigationFragment) {
            navigationFragment = (NavigationFragment) fragment;
            navigationFragment.setSelected(getNavigationScreen());
        }
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override protected void onResume() {
        super.onResume();
        if (navigationFragment != null) {
            navigationFragment.setSelected(getNavigationScreen());
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

    @Override public void onNavigationItemSelected(final NavigationScreen navigationScreen) {
        if (isSameNavigationScreen(navigationScreen)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }

        handler.postDelayed(new Runnable() {
            @Override public void run() {
                switch (navigationScreen) {
                    case User:
                        if (user.isLoggedIn()) {
                            ProfileActivity.start(DrawerActivity.this);
                        } else {
                            BaseLoginActivity.start(DrawerActivity.this);
                        }
                        break;
                    case Overview:
                        OverviewActivity.start(DrawerActivity.this);
                        break;
                    case Accounts:
                        AccountsActivity.startView(DrawerActivity.this);
                        break;
                    case Transactions:
                        TransactionsActivity.startView(DrawerActivity.this);
                        break;
                    case Reports:
                        CategoriesReportActivity.start(DrawerActivity.this);
                        break;
                    case Settings:
                        SettingsActivity.start(DrawerActivity.this);
                        break;
                }

                if (!(DrawerActivity.this instanceof OverviewActivity)) {
                    finish();
                }
            }
        }, DRAWER_LAUNCH_DELAY);
    }

    @Nullable protected NavigationScreen getNavigationScreen() {
        return null;
    }

    private boolean isSameNavigationScreen(NavigationScreen navigationScreen) {
        switch (navigationScreen) {
            case User:
                return user.isLoggedIn() ? this instanceof ProfileActivity : this instanceof BaseLoginActivity;
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
