package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;

import java.util.ArrayList;
import java.util.List;




public class NavigationBottomAdapter extends BaseAdapter {
    private final Context context;
    private final List<NavigationAdapter.NavigationItem> items;
    private final int selectedTextColor;
    private final int normalTextColor;

    private NavigationAdapter.NavigationScreen selectedNavigationScreen;

    public NavigationBottomAdapter(Context context) {
        this.context = context;

        items = new ArrayList<>();
        items.add(new NavigationAdapter.NavigationItem(NavigationAdapter.NavigationScreen.Settings, context.getString(R.string.settings), R.drawable.ic_settings_black_24dp));
        items.add(new NavigationAdapter.NavigationItem(NavigationAdapter.NavigationScreen.About, context.getString(R.string.about), R.drawable.ic_info_outline_black_24dp));

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

        final NavigationAdapter.NavigationItem item = items.get(position);
        holder.title_TV.setText(item.getTitle());
        holder.title_TV.setTextColor(item.getNavigationScreen() == selectedNavigationScreen ? selectedTextColor : normalTextColor);
        holder.icon_IV.setImageResource(item.getIcon());

        return view;
    }

    public void setSelectedNavigationScreen(NavigationAdapter.NavigationScreen navigationScreen) {
        this.selectedNavigationScreen = navigationScreen;
        notifyDataSetChanged();
    }

    public static class NavigationItem {
        private final NavigationAdapter.NavigationScreen navigationScreen;
        private final String title;
        private int icon;

        public NavigationItem(NavigationAdapter.NavigationScreen navigationScreen, String title, int icon) {
            this.navigationScreen = navigationScreen;
            this.title = title;
            this.icon = icon;
        }

        public NavigationAdapter.NavigationScreen getNavigationScreen() {
            return navigationScreen;
        }

        public int getId() {
            return navigationScreen.getId();
        }

        public String getTitle() {
            return title;
        }

        public int getIcon() {
            return icon;
        }
    }

    private static class ViewHolder {
        public TextView title_TV;
        public ImageView icon_IV;

        public static ViewHolder setAsTag(View view) {
            final ViewHolder holder = new ViewHolder();

            holder.title_TV = (TextView) view.findViewById(R.id.titleTextView);
            holder.icon_IV = (ImageView) view.findViewById(R.id.drawer_item_icon);

            view.setTag(holder);

            return holder;
        }
    }
}
