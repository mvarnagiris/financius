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
    public static final int NAV_ID_OVERVIEW = 1;
    public static final int NAV_ID_ACCOUNTS = 2;
    public static final int NAV_ID_TRANSACTIONS = 3;

    private final Context context;
    private final List<NavigationItem> items;

    private long selectedId;

    public NavigationAdapter(Context context) {
        this.context = context;

        items = new ArrayList<>();
        items.add(new NavigationItem(NAV_ID_OVERVIEW, context.getString(R.string.overview)));
        items.add(new NavigationItem(NAV_ID_ACCOUNTS, context.getString(R.string.accounts)));
        items.add(new NavigationItem(NAV_ID_TRANSACTIONS, context.getString(R.string.transactions)));
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

        holder.title_TV.setText(items.get(position).getTitle());

        return view;
    }

    public void setSelectedId(long selectedId) {
        this.selectedId = selectedId;
    }

    public NavigationItem getSelectedItem() {
        for (NavigationItem item : items) {
            if (item.getId() == selectedId) {
                return item;
            }
        }

        return null;
    }

    public static class NavigationItem {
        private final int id;
        private final String title;

        public NavigationItem(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    private static class ViewHolder {
        public TextView title_TV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();
            holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
            view.setTag(holder);

            return holder;
        }
    }
}
