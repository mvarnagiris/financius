package com.code44.finance.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.ui.ItemListFragment;
import com.code44.finance.ui.OverviewFragment;
import com.code44.finance.ui.accounts.AccountListFragment;
import com.code44.finance.ui.backup.BackupFragment;
import com.code44.finance.ui.backup.YourDataActivity;
import com.code44.finance.ui.backup.YourDataFragment;
import com.code44.finance.ui.reports.CategoriesReportFragment;
import com.code44.finance.ui.transactions.TransactionListFragment;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter extends BaseAdapter
{
    private final Context context;
    private final List<NavItemInfo> navList = new ArrayList<NavItemInfo>();
    private int selectedPosition = 0;

    public NavigationAdapter(final Context context)
    {
        this.context = context;

        final Resources res = context.getResources();

        navList.add(new NavItemInfo(res.getString(R.string.overview), OverviewFragment.class.getName(), null));
        navList.add(new NavItemInfo(res.getString(R.string.accounts), AccountListFragment.class.getName(), AccountListFragment.makeArgs(ItemListFragment.SELECTION_TYPE_NONE, null)));
        navList.add(new NavItemInfo(res.getString(R.string.transactions), TransactionListFragment.class.getName(), null));
        //navList.add(new NavItemInfo(res.getString(R.string.budgets), BudgetListFragment.class.getName(), null));
        navList.add(new NavItemInfo(res.getString(R.string.reports), CategoriesReportFragment.class.getName(), null));
        //navList.add(new NavItemInfo(res.getString(R.string.wish_list), OverviewFragment.class.getName(), null));
        navList.add(new NavItemInfo(res.getString(R.string.backup),
                new runActivityCallbacks(){
                    @Override
                    public void onSelected()
                    {
                        YourDataActivity.start(context);
                    }
                }, null));

    }

    @Override
    public int getCount()
    {
        return navList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return navList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup)
    {
        final ViewHolder holder;
        if (view == null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.li_navigation, viewGroup, false);
            holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            holder.badge_TV = (TextView) view.findViewById(R.id.badge_TV);
            view.setTag(holder);
        }
        else
            holder = (ViewHolder) view.getTag();

        final NavItemInfo item = navList.get(position);
        holder.title_TV.setText(item.getTitle());
        if (item.getBadge() > 0)
        {
            holder.badge_TV.setText(String.valueOf(item.getBadge()));
            holder.badge_TV.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.badge_TV.setVisibility(View.INVISIBLE);
        }

        view.setBackgroundColor(position == selectedPosition ? context.getResources().getColor(R.color.f_brand_lighter2) : 0);
        return view;
    }

    public int getSelectedPosition()
    {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition)
    {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    public static class NavItemInfo
    {
        private final String title;
        private final String fragmentClassName;


        private final runActivityCallbacks activity;
        private final Bundle args;
        private int badge;

        public NavItemInfo(String title, String fragmentClassName, Bundle args)
        {
            this.title = title;
            this.fragmentClassName = fragmentClassName;
            this.args = args;
            badge = 0;
            activity = null;
        }

        public NavItemInfo(String title, runActivityCallbacks fragmentActivity, Bundle args)
        {
            this.title = title;
            this.fragmentClassName = null;
            this.args = args;
            badge = 0;
            activity = fragmentActivity;
        }

        public String getTitle()
        {
            return title;
        }

        public runActivityCallbacks getActivity()
        {
            return activity;
        }

        public String getFragmentClassName()
        {
            return fragmentClassName;
        }

        public Bundle getArgs()
        {
            return args;
        }

        public int getBadge()
        {
            return badge;
        }

        public void setBadge(int badge)
        {
            this.badge = badge;
        }
    }

    private static class ViewHolder
    {
        public TextView title_TV;
        public TextView badge_TV;
    }

    public static interface runActivityCallbacks
    {
        public void onSelected();
    }
}