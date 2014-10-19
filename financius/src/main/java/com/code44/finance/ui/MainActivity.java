package com.code44.finance.ui;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.services.StartupService;
import com.code44.finance.ui.accounts.AccountsFragment;
import com.code44.finance.ui.overview.OverviewFragment;
import com.code44.finance.ui.reports.CategoriesReportFragment;
import com.code44.finance.ui.transactions.TransactionsFragment;
import com.code44.finance.ui.user.UserFragment;
import com.code44.finance.utils.analytics.Analytics;

public class MainActivity extends BaseActivity implements NavigationFragment.NavigationListener {
    private static final String FRAGMENT_CONTENT = "FRAGMENT_CONTENT";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        // Get views
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Setup
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, (Toolbar) findViewById(R.id.toolbar), R.string.open_navigation, R.string.close_navigation);
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            StartupService.start(this);
        }

        final BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CONTENT);
        if (fragment == null) {
            ((NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_F)).select(NavigationAdapter.NAV_ID_OVERVIEW);
        }

        getEventBus().register(this);
    }

    @Override public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!(fragment instanceof NavigationFragment)) {
                if (fragment instanceof OverviewFragment || fragment instanceof CategoriesReportFragment) {
                    getToolbar().setElevation(0);
                } else {
                    getToolbar().setElevation(getResources().getDimension(R.dimen.elevation_header));
                }
            }
        }
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        getEventBus().unregister(this);
    }

    @Override public void onBackPressed() {
        // Select OverviewFragment before quitting the app
        if (!(getSupportFragmentManager().findFragmentByTag(FRAGMENT_CONTENT) instanceof OverviewFragment)) {
            ((NavigationFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_F)).select(NavigationAdapter.NAV_ID_OVERVIEW);
        } else {
            super.onBackPressed();
        }
    }

    @Override public void onNavigationItemSelected(NavigationAdapter.NavigationItem item) {
        final BaseFragment baseFragment;
        switch (item.getId()) {
            case NavigationAdapter.NAV_ID_USER:
                baseFragment = UserFragment.newInstance();
                getSupportActionBar().setTitle(R.string.user);
                break;

            case NavigationAdapter.NAV_ID_OVERVIEW:
                baseFragment = OverviewFragment.newInstance();
                getAnalytics().trackScreen(Analytics.Screen.Overview);
                getSupportActionBar().setTitle(R.string.overview);
                break;

            case NavigationAdapter.NAV_ID_ACCOUNTS:
                baseFragment = AccountsFragment.newInstance(ModelListFragment.Mode.VIEW);
                getAnalytics().trackScreen(Analytics.Screen.AccountList);
                getSupportActionBar().setTitle(R.string.accounts);
                break;

            case NavigationAdapter.NAV_ID_TRANSACTIONS:
                baseFragment = TransactionsFragment.newInstance();
                getAnalytics().trackScreen(Analytics.Screen.TransactionList);
                getSupportActionBar().setTitle(R.string.transactions);
                break;

            case NavigationAdapter.NAV_ID_REPORTS:
                baseFragment = CategoriesReportFragment.newInstance();
                getAnalytics().trackScreen(Analytics.Screen.CategoriesReport);
                getSupportActionBar().setTitle(R.string.categories_report);
                break;

            default:
                baseFragment = null;
                break;
        }

        drawerLayout.closeDrawers();

        if (baseFragment != null) {
            loadFragment(baseFragment);
        }
    }

    private void loadFragment(BaseFragment fragment) {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.content, fragment, FRAGMENT_CONTENT);
        if (fm.findFragmentByTag(FRAGMENT_CONTENT) != null) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        }
        ft.commit();
    }
}
