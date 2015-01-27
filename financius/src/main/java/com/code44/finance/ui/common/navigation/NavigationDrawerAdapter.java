package com.code44.finance.ui.common.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final int defaultTextColor;
    private final int defaultIconColor;
    private final int selectedColor;
    private final int selectedBackgroundColor;
    private final List<NavigationItem> navigationItems;
    private NavigationScreen selectedNavigationScreen;

    public NavigationDrawerAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
        defaultTextColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        defaultIconColor = ThemeUtils.getColor(context, R.attr.actionItemColor);
        selectedColor = ThemeUtils.getColor(context, R.attr.colorPrimary);
        selectedBackgroundColor = ThemeUtils.getColor(context, R.attr.backgroundColorSecondary);

        navigationItems = new ArrayList<>();
        navigationItems.add(new HeaderNavigationItem());
        navigationItems.add(new DividerNavigationItem(false));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Overview, R.drawable.ic_action_overview, R.string.overview));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Accounts, R.drawable.ic_action_account, R.string.accounts_other));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Transactions, R.drawable.ic_action_transactions, R.string.transactions_other));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Reports, R.drawable.ic_action_reports, R.string.reports));
        navigationItems.add(new DividerNavigationItem(true));
        navigationItems.add(new SecondaryNavigationItem(NavigationScreen.Settings, R.string.settings));
    }

    @Override
    public int getViewTypeCount() {
        return ViewType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType().ordinal();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).getViewType() != ViewType.Divider;
    }

    @Override
    public int getCount() {
        return navigationItems.size();
    }

    @Override
    public NavigationItem getItem(int position) {
        return navigationItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        NavigationItem item = getItem(position);
        if (convertView == null) {
            convertView = item.inflate(parent);
            holder = item.createViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        item.bind(holder);
        return convertView;
    }

    public void setSelectedNavigationScreen(NavigationScreen navigationScreen) {
        this.selectedNavigationScreen = navigationScreen;
        notifyDataSetChanged();
    }

    public static enum ViewType {
        Header, Primary, Secondary, Divider
    }

    private static class ViewHolder {
        private final View view;

        private ViewHolder(View view) {
            this.view = view;
            view.setTag(this);
        }

        public View getView() {
            return view;
        }
    }

    private static class PrimaryViewHolder extends ViewHolder {
        public final ImageView iconImageView;
        public final TextView titleTextView;

        private PrimaryViewHolder(View view) {
            super(view);
            iconImageView = (ImageView) view.findViewById(R.id.iconImageView);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        }
    }

    private static class SecondaryViewHolder extends ViewHolder {
        public final TextView titleTextView;

        private SecondaryViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        }
    }

    private static class HeaderViewHolder extends ViewHolder {
        private HeaderViewHolder(View view) {
            super(view);
        }
    }

    public abstract static class NavigationItem {
        private final NavigationScreen navigationScreen;
        private final NavigationDrawerAdapter.ViewType viewType;

        private NavigationItem(NavigationScreen navigationScreen, ViewType viewType) {
            this.navigationScreen = navigationScreen;
            this.viewType = viewType;
        }

        public NavigationScreen getNavigationScreen() {
            return navigationScreen;
        }

        public ViewType getViewType() {
            return viewType;
        }

        public abstract View inflate(ViewGroup parent);

        public abstract ViewHolder createViewHolder(View view);

        public abstract void bind(ViewHolder holder);
    }

    private class PrimaryNavigationItem extends NavigationItem {
        private final int iconResId;
        private final int titleResId;

        private PrimaryNavigationItem(NavigationScreen navigationScreen, int iconResId, int titleResId) {
            super(navigationScreen, ViewType.Primary);
            this.iconResId = iconResId;
            this.titleResId = titleResId;
        }

        @Override
        public View inflate(ViewGroup parent) {
            return inflater.inflate(R.layout.li_navigation, parent, false);
        }

        @Override
        public ViewHolder createViewHolder(View view) {
            return new PrimaryViewHolder(view);
        }

        @Override
        public void bind(ViewHolder holder) {
            final PrimaryViewHolder viewHolder = (PrimaryViewHolder) holder;
            final boolean isSelected = getNavigationScreen() == selectedNavigationScreen;
            viewHolder.iconImageView.setImageResource(iconResId);
            viewHolder.iconImageView.setColorFilter(isSelected ? selectedColor : defaultIconColor);
            viewHolder.titleTextView.setText(titleResId);
            viewHolder.titleTextView.setTextColor(isSelected ? selectedColor : defaultTextColor);
            viewHolder.getView().setBackgroundColor(isSelected ? selectedBackgroundColor : 0);
        }
    }

    private class SecondaryNavigationItem extends NavigationItem {
        private final int titleResId;

        private SecondaryNavigationItem(NavigationScreen navigationScreen, int titleResId) {
            super(navigationScreen, ViewType.Secondary);
            this.titleResId = titleResId;
        }

        @Override
        public View inflate(ViewGroup parent) {
            final View view = inflater.inflate(R.layout.li_navigation, parent, false);
            view.findViewById(R.id.iconImageView).setVisibility(View.GONE);
            return view;
        }

        @Override
        public ViewHolder createViewHolder(View view) {
            return new SecondaryViewHolder(view);
        }

        @Override
        public void bind(ViewHolder holder) {
            final SecondaryViewHolder viewHolder = (SecondaryViewHolder) holder;
            final boolean isSelected = getNavigationScreen() == selectedNavigationScreen;
            viewHolder.titleTextView.setText(titleResId);
            viewHolder.titleTextView.setTextColor(isSelected ? selectedColor : defaultTextColor);
            viewHolder.getView().setBackgroundColor(isSelected ? selectedBackgroundColor : 0);
        }
    }

    private class HeaderNavigationItem extends NavigationItem {
        private HeaderNavigationItem() {
            super(NavigationScreen.User, ViewType.Header);
        }

        @Override
        public View inflate(ViewGroup parent) {
            return inflater.inflate(R.layout.li_navigation_header, parent, false);
        }

        @Override
        public ViewHolder createViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void bind(ViewHolder holder) {
        }
    }

    private class DividerNavigationItem extends NavigationItem {
        private final boolean showLine;

        private DividerNavigationItem(boolean showLine) {
            super(null, ViewType.Divider);
            this.showLine = showLine;
        }

        @Override
        public View inflate(ViewGroup parent) {
            return inflater.inflate(R.layout.li_navigation_divider, parent, false);
        }

        @Override
        public ViewHolder createViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        public void bind(ViewHolder holder) {
            ((ViewGroup) holder.getView()).getChildAt(0).setVisibility(showLine ? View.VISIBLE : View.GONE);
        }
    }
}
