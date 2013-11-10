package com.code44.finance.adapters;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener
{
    private final Context context;
    private final ActionBar actionBar;
    private final ViewPager viewPager;
    private final ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();

    public TabsAdapter(FragmentActivity activity, ViewPager pager)
    {
        super(activity.getSupportFragmentManager());
        this.context = activity;
        this.actionBar = activity.getActionBar();
        this.viewPager = pager;
        this.viewPager.setAdapter(this);
        this.viewPager.setOnPageChangeListener(this);
    }

    // Public methods
    // --------------------------------------------------------------------------------------------------------------------------

    public void addTab(Tab tab, Class<?> clss, Bundle args)
    {
        TabInfo info = new TabInfo(clss, args);
        tab.setTag(info);
        tab.setTabListener(this);
        tabs.add(info);
        actionBar.addTab(tab);
        notifyDataSetChanged();
    }

    public void setArgs(int position, Bundle args)
    {
        tabs.get(position).setArgs(args);
    }

    // FragmentPagerAdapter
    // --------------------------------------------------------------------------------------------------------------------------

    @Override
    public int getCount()
    {
        return tabs.size();
    }

    @Override
    public Fragment getItem(int position)
    {
        TabInfo info = tabs.get(position);
        return Fragment.instantiate(context, info.clss.getName(), info.args);
    }

    // ViewPager.OnPageChangeListener
    // --------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
        actionBar.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
    }

    // ActionBar.TabListener
    // --------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft)
    {
        if (viewPager.getCurrentItem() != tab.getPosition())
            viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft)
    {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft)
    {
    }

    // TabInfo
    // --------------------------------------------------------------------------------------------------------------------------

    static final class TabInfo
    {
        private final Class<?> clss;
        private Bundle args;

        TabInfo(Class<?> _class, Bundle _args)
        {
            clss = _class;
            args = _args;
        }

        public void setArgs(Bundle args)
        {
            this.args = args;
        }
    }
}