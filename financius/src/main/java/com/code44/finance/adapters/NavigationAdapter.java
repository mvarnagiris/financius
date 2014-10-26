package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.code44.finance.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter extends BaseAdapter {
    private final Context context;
    private final List<NavigationItem> items;
    private final int selectedTextColor;
    private final int normalTextColor;

    private NavigationScreen selectedNavigationScreen;

    public NavigationAdapter(Context context) {
        this.context = context;

        items = new ArrayList<>();
        // TODO items.add(new NavigationItem(NAV_ID_USER, context.getString(R.string.user)));
        items.add(new NavigationItem(NavigationScreen.Overview, context.getString(R.string.overview)));
        items.add(new NavigationItem(NavigationScreen.Accounts, context.getString(R.string.accounts)));
        items.add(new NavigationItem(NavigationScreen.Transactions, context.getString(R.string.transactions)));
        items.add(new NavigationItem(NavigationScreen.Reports, context.getString(R.string.reports)));

        selectedTextColor = context.getResources().getColor(R.color.text_brand);
        normalTextColor = context.getResources().getColor(R.color.text_primary);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.li_navigation, parent, false);
            holder = ViewHolder.setAsTag(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final NavigationItem item = items.get(position);
        holder.title_TV.setText(item.getTitle());
        holder.title_TV.setTextColor(item.getNavigationScreen() == selectedNavigationScreen ? selectedTextColor : normalTextColor);

        return view;
    }

    public void setSelectedNavigationScreen(NavigationScreen navigationScreen) {
        this.selectedNavigationScreen = navigationScreen;
        notifyDataSetChanged();
    }

    public static enum NavigationScreen {
        User(1), Overview(2), Accounts(3), Transactions(4), Reports(5);
        private final int id;

        NavigationScreen(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class NavigationItem {
        private final NavigationScreen navigationScreen;
        private final String title;

        public NavigationItem(NavigationScreen navigationScreen, String title) {
            this.navigationScreen = navigationScreen;
            this.title = title;
        }

        public NavigationScreen getNavigationScreen() {
            return navigationScreen;
        }

        public int getId() {
            return navigationScreen.getId();
        }

        public String getTitle() {
            return title;
        }
    }

    private static class ViewHolder {
        public TextView title_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);

            return holder;
        }
    }
}
