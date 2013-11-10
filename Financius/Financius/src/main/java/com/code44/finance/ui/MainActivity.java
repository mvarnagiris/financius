package com.code44.finance.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.utils.ExchangeRatesHelper;
import com.code44.finance.utils.FilterHelper;
import com.code44.finance.utils.LayoutType;
import com.crashlytics.android.Crashlytics;
import de.greenrobot.event.EventBus;

public class MainActivity extends AbstractActivity implements NavigationFragment.Callbacks
{
    private static final String FRAGMENT_CONTENT = "FRAGMENT_CONTENT";
    private NavigationContentFragment content_F;
    private DrawerLayout drawer_V;
    private ActionBarDrawerToggle drawerToggle_V;

    @SuppressWarnings("UnusedDeclaration")
    public static void startMain(Context context)
    {
        Intent intent = makeIntent(context);
        context.startActivity(intent);
    }

    public static Intent makeIntent(Context context)
    {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get fragments
        content_F = (NavigationContentFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_CONTENT);

        // Update title if necessary
        if (content_F != null)
            setActionBarTitle(content_F.getTitle());

        // Get views
        if (!LayoutType.isTabletLandscape(this))
        {
            drawer_V = (DrawerLayout) findViewById(R.id.drawer_V);
            drawerToggle_V = new ActionBarDrawerToggle(this, drawer_V, R.drawable.ic_drawer, R.string.open_navigation, R.string.close_navigation)
            {
                @Override
                public void onDrawerOpened(View drawerView)
                {
                    super.onDrawerOpened(drawerView);

                    // Set actionbar title according to which fragment is opened
                    if (drawer_V.isDrawerOpen(Gravity.START))
                        setActionBarTitle(R.string.app_name);
                    else if (drawer_V.isDrawerOpen(Gravity.END))
                        setActionBarTitle(R.string.filter);
                }

                @Override
                public void onDrawerClosed(View drawerView)
                {
                    super.onDrawerClosed(drawerView);

                    // Check maybe one of the fragments is still open
                    if (drawer_V.isDrawerOpen(Gravity.START))
                        setActionBarTitle(R.string.app_name);
                    else if (drawer_V.isDrawerOpen(Gravity.END))
                        setActionBarTitle(R.string.filter);
                    else
                    {
                        // None of the drawers are open
                        if (content_F != null && ((Fragment) content_F).isAdded())
                            setActionBarTitle(content_F.getTitle());
                        else
                            //noinspection ConstantConditions
                            getActionBar().setTitle(null);
                    }
                }

                @Override
                public void onDrawerStateChanged(int newState)
                {
                    super.onDrawerStateChanged(newState);
                }
            };
        }

        // Setup
        if (!LayoutType.isTabletLandscape(this))
        {
            drawer_V.setDrawerListener(drawerToggle_V);
            drawer_V.setDrawerShadow(R.drawable.drawer_shadow_right, Gravity.LEFT);
            drawer_V.setDrawerShadow(R.drawable.drawer_shadow_left, Gravity.RIGHT);
        }

        // Force security on this Activity
        if (savedInstanceState == null)
            setForceSecurity(true);

        Crashlytics.start(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        if (!LayoutType.isTabletLandscape(this))
        {
            // Sync the toggle state after onRestoreInstanceState has occurred.
            drawerToggle_V.syncState();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // After forcing security once, don't force it any more
        setForceSecurity(false);

        // Update exchange rates if necessary
        ExchangeRatesHelper.getDefault(this).startExchangeRateUpdatesIfNecessary();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        if (!LayoutType.isTabletLandscape(this))
        {
            drawerToggle_V.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (!LayoutType.isTabletLandscape(this))
        {
            // Close filter drawer if necessary
            if (item.getItemId() == android.R.id.home && drawer_V.isDrawerVisible(Gravity.END))
            {
                drawer_V.closeDrawer(Gravity.END);
                return true;
            }
        }

        if (!LayoutType.isTabletLandscape(this))
        {
            // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
            if (drawerToggle_V.onOptionsItemSelected(item))
                return true;
        }

        // For some reason the code above does not return true. So this is here as a workaround to consume "up" event
        if (item.getItemId() == android.R.id.home)
            return true;

        if (!LayoutType.isTabletLandscape(this))
        {
            switch (item.getItemId())
            {
                case R.id.action_notifications:
                {
                    // Close notifications drawer if necessary
                    if (drawer_V.isDrawerVisible(Gravity.START))
                        drawer_V.closeDrawer(Gravity.START);

                    if (drawer_V.isDrawerVisible(Gravity.END))
                        drawer_V.closeDrawers();
                    else
                        drawer_V.openDrawer(Gravity.END);
                    break;
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (content_F == null || content_F instanceof OverviewFragment)
            super.onBackPressed();
        else
            EventBus.getDefault().post(new NavigationFragment.NavigationEvent(NavigationFragment.NavigationEvent.TYPE_OVERVIEW));
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);

        FilterHelper.getDefault(this).clearAll();
        if (fragment instanceof NavigationContentFragment)
        {
            setActionBarTitle(((NavigationContentFragment) fragment).getTitle());
            if (drawer_V != null && !LayoutType.isTabletLandscape(this))
            {
                drawer_V.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        drawer_V.closeDrawers();
                    }
                }, 100);
            }
        }
    }

    @Override
    public void onNavItemSelected(String fragmentName)
    {
        if (content_F == null || !content_F.getClass().getName().equals(fragmentName))
        {
            final Bundle args = new Bundle();
            args.putBoolean(ItemListFragment.ARG_IS_OPEN_DRAWER_LAYOUT, true);

            final Fragment f = Fragment.instantiate(this, fragmentName, args);
            content_F = (NavigationContentFragment) f;
            getSupportFragmentManager().beginTransaction().replace(R.id.content_V, f, FRAGMENT_CONTENT).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
        }
        else
        {
            if (!LayoutType.isTabletLandscape(this))
                drawer_V.closeDrawers();
        }
    }

    public static interface NavigationContentFragment
    {
        public String getTitle();
    }
}