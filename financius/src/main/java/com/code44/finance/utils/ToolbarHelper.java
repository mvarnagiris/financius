package com.code44.finance.utils;

import android.app.Activity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toolbar;

import com.code44.finance.R;

public class ToolbarHelper {
    private final Toolbar toolbar;
    private final DrawerLayout drawer;

    public ToolbarHelper(Activity activity) {
        // Get views
        toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        drawer = (DrawerLayout) activity.findViewById(R.id.drawer);

        // Setup
        if (toolbar != null) {
            activity.setActionBar(toolbar);

            if (drawer != null) {
                toolbar.setNavigationIcon(R.drawable.ic_drawer);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (drawer.isDrawerOpen(Gravity.START)) {
                            drawer.closeDrawer(Gravity.START);
                        } else {
                            drawer.openDrawer(Gravity.START);
                        }
                    }
                });
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawer != null && item.getItemId() == android.R.id.home) {
            if (drawer.isDrawerOpen(Gravity.START)) {
                drawer.closeDrawer(Gravity.START);
            } else {
                drawer.openDrawer(Gravity.START);
            }
            return true;
        }
        return false;
    }

    public void setTitle(int resId) {
        toolbar.setTitle(resId);
    }

    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {
        public CustomActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes);
        }
    }
}
