package com.code44.finance.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.ui.accounts.AccountsFragment;
import com.code44.finance.ui.transactions.TransactionsFragment;

public class MainActivity extends BaseActivity implements NavigationFragment.NavigationListener, ModelListFragment.ModelListFragmentCallbacks {
    private static final String FRAGMENT_CONTENT = "FRAGMENT_CONTENT";

    private DrawerLayout drawer_V;
    private ActionBarDrawerToggle drawerToggle_V;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup ActionBar
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;

        // Get views
        drawer_V = (DrawerLayout) findViewById(R.id.drawer_V);
        drawerToggle_V = new CustomActionBarDrawerToggle(this, drawer_V, R.drawable.ic_navigation, R.string.open_navigation, R.string.close_navigation);

        // Setup drawer
        drawer_V.setDrawerListener(drawerToggle_V);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle_V.syncState();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        // Close navigation drawer fragment is attached, except for NavigationFragment
        if (!(fragment instanceof NavigationFragment)) {
            if (drawer_V != null) {
                drawer_V.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        drawer_V.closeDrawers();
                    }
                }, 0);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if (drawerToggle_V.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        // For some reason mDrawerToggleV.onOptionsItemSelected(item) does not return true.
        return item.getItemId() == android.R.id.home || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle_V.onConfigurationChanged(newConfig);
    }

    @Override
    public void onNavigationItemSelected(NavigationAdapter.NavigationItem item) {
        String title = null;
        BaseFragment baseFragment = null;
        switch (item.getId()) {
            case NavigationAdapter.NAV_ID_OVERVIEW:
                baseFragment = OverviewFragment.newInstance();
                title = getString(R.string.overview);
                break;

            case NavigationAdapter.NAV_ID_ACCOUNTS:
                baseFragment = AccountsFragment.newInstance(ModelListActivity.Mode.VIEW);
                title = getString(R.string.accounts);
                break;

            case NavigationAdapter.NAV_ID_TRANSACTIONS:
                baseFragment = TransactionsFragment.newInstance();
                title = getString(R.string.transactions);
                break;
        }

        setActionBarTitle(title);
        if (baseFragment != null) {
            loadFragment(baseFragment);
        }
    }

    private void loadFragment(BaseFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_V, fragment, FRAGMENT_CONTENT)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onModelClickListener(View view, int position, long modelId, BaseModel model) {
        // TODO Load account and transactions
    }

    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {
        public CustomActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
        }
    }
}
