package com.code44.finance.ui.common.navigation;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.ui.common.recycler.ClickViewHolder;
import com.code44.finance.utils.ThemeUtils;
import com.code44.finance.utils.picasso.PicassoUtils;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
    private final User user;
    private final ClickViewHolder.OnItemClickListener onItemClickListener;
    private final int defaultTextColor;
    private final int defaultIconColor;
    private final int selectedColor;
    private final int selectedBackgroundColor;
    private final List<NavigationItem> navigationItems;

    private NavigationScreen selectedNavigationScreen;

    public NavigationDrawerAdapter(@NonNull Context context, @NonNull User user, @NonNull ClickViewHolder.OnItemClickListener onItemClickListener) {
        this.user = checkNotNull(user, "User cannot be null.");
        this.onItemClickListener = checkNotNull(onItemClickListener, "OnItemClickListener cannot be null.");

        checkNotNull(context, "Context cannot be null.");
        defaultTextColor = ThemeUtils.getColor(context, android.R.attr.textColorPrimary);
        defaultIconColor = ThemeUtils.getColor(context, R.attr.colorIcon);
        selectedColor = ThemeUtils.getColor(context, R.attr.colorPrimary);
        selectedBackgroundColor = ThemeUtils.getColor(context, R.attr.backgroundColorSecondary);

        navigationItems = new ArrayList<>();
        navigationItems.add(new HeaderNavigationItem());
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Overview, R.drawable.ic_action_overview, R.string.overview));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Accounts, R.drawable.ic_action_account, R.string.accounts_other));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Transactions, R.drawable.ic_action_transactions, R.string.transactions_other));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Reports, R.drawable.ic_action_reports, R.string.reports));
        navigationItems.add(new PrimaryNavigationItem(NavigationScreen.Settings, R.drawable.ic_action_settings, R.string.settings));
    }

    @Override public int getItemCount() {
        return navigationItems.size();
    }

    @Override public long getItemId(int position) {
        return getItem(position).getNavigationScreen().getId();
    }

    @Override public int getItemViewType(int position) {
        return getItem(position).getViewType().ordinal();
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ViewType.values()[viewType].inflate(parent), onItemClickListener);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        getItem(position).bind(holder);
    }

    public NavigationItem getItem(int position) {
        return navigationItems.get(position);
    }

    public void setSelectedNavigationScreen(NavigationScreen navigationScreen) {
        this.selectedNavigationScreen = navigationScreen;
        notifyDataSetChanged();
    }

    private enum ViewType {
        Header {
            @Override public View inflate(ViewGroup parent) {
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.li_navigation_header, parent, false);
            }
        },
        Primary {
            @Override public View inflate(ViewGroup parent) {
                return LayoutInflater.from(parent.getContext()).inflate(R.layout.li_navigation, parent, false);
            }
        };

        public abstract View inflate(ViewGroup parent);
    }

    private class PrimaryNavigationItem extends NavigationItem {
        private final int iconResId;
        private final int titleResId;

        private PrimaryNavigationItem(@NonNull NavigationScreen navigationScreen, @DrawableRes int iconResId, @StringRes int titleResId) {
            super(navigationScreen, ViewType.Primary);
            this.iconResId = iconResId;
            this.titleResId = titleResId;
        }

        @Override public void bind(ViewHolder holder) {
            final boolean isSelected = getNavigationScreen() == selectedNavigationScreen;
            holder.iconImageView.setImageResource(iconResId);
            holder.iconImageView.setColorFilter(isSelected ? selectedColor : defaultIconColor);
            holder.titleTextView.setText(titleResId);
            holder.titleTextView.setTextColor(isSelected ? selectedColor : defaultTextColor);
            holder.itemView.setBackgroundColor(isSelected ? selectedBackgroundColor : 0);
        }
    }

    private class HeaderNavigationItem extends NavigationItem {
        private HeaderNavigationItem() {
            super(NavigationScreen.User, ViewType.Header);
        }

        @Override public void bind(ViewHolder holder) {
            holder.titleTextView.setText(user.getName());
            PicassoUtils.loadUserCover(holder.coverImageView, user.getCoverUrl());
            PicassoUtils.loadUserPhoto(holder.iconImageView, user.getPhotoUrl());
        }
    }

    static class ViewHolder extends ClickViewHolder {
        private final ImageView coverImageView;
        private final ImageView iconImageView;
        private final TextView titleTextView;

        private ViewHolder(@NonNull android.view.View itemView, @Nullable OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            coverImageView = (ImageView) itemView.findViewById(R.id.coverImageView);
            iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }

    public abstract static class NavigationItem {
        private final NavigationScreen navigationScreen;
        private final ViewType viewType;

        private NavigationItem(@NonNull NavigationScreen navigationScreen, @NonNull ViewType viewType) {
            this.navigationScreen = checkNotNull(navigationScreen, "NavigationScreen cannot be null.");
            this.viewType = checkNotNull(viewType, "ViewType cannot be null.");
        }

        public NavigationScreen getNavigationScreen() {
            return navigationScreen;
        }

        private ViewType getViewType() {
            return viewType;
        }

        protected abstract void bind(ViewHolder holder);
    }
}
