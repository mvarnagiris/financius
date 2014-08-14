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
    public static final int NAV_ID_USER = 1;
    public static final int NAV_ID_OVERVIEW = 2;
    public static final int NAV_ID_ACCOUNTS = 3;
    public static final int NAV_ID_TRANSACTIONS = 4;

    private final Context context;
    private final List<NavigationItem> items;
    private final int selectedTextColor;
    private final int normalTextColor;

    private long selectedId;

    public NavigationAdapter(Context context) {
        this.context = context;

        items = new ArrayList<>();
        // TODO items.add(new NavigationItem(NAV_ID_USER, context.getString(R.string.user)));
        items.add(new NavigationItem(NAV_ID_OVERVIEW, context.getString(R.string.overview)));
        items.add(new NavigationItem(NAV_ID_ACCOUNTS, context.getString(R.string.accounts)));
        items.add(new NavigationItem(NAV_ID_TRANSACTIONS, context.getString(R.string.transactions)));

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
        holder.title_TV.setTextColor(item.getId() == selectedId ? selectedTextColor : normalTextColor);

        return view;
    }

    public void setSelectedId(long selectedId) {
        this.selectedId = selectedId;
        notifyDataSetChanged();
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
