package com.code44.finance.ui.common.navigation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.ui.common.fragments.BaseFragment;
import com.code44.finance.ui.common.recycler.ClickViewHolder;
import com.code44.finance.ui.common.recycler.DividerDecoration;

import javax.inject.Inject;

public class NavigationFragment extends BaseFragment implements ClickViewHolder.OnItemClickListener {
    @Inject User user;

    private NavigationDrawerAdapter adapter;
    private NavigationListener navigationListener;
    private NavigationScreen pendingNavigationScreen;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationListener) {
            navigationListener = (NavigationListener) activity;
        } else {
            throw new IllegalArgumentException("Activity " + activity.getClass()
                    .getName() + " must implement " + NavigationListener.class.getName());
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        // Setup
        adapter = new NavigationDrawerAdapter(getActivity(), user, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new NavigationDividerDecoration(getActivity(), getResources().getDimensionPixelSize(R.dimen.space_normal)));
        recyclerView.setAdapter(adapter);

        if (pendingNavigationScreen != null) {
            adapter.setSelectedNavigationScreen(pendingNavigationScreen);
            pendingNavigationScreen = null;
        }
    }

    @Override public void onItemClick(View view, int position) {
        final NavigationScreen navigationScreen = adapter.getItem(position).getNavigationScreen();
        adapter.setSelectedNavigationScreen(navigationScreen);
        navigationListener.onNavigationItemSelected(navigationScreen);
    }

    public void setSelected(NavigationScreen navigationScreen) {
        if (adapter == null) {
            pendingNavigationScreen = navigationScreen;
        } else {
            adapter.setSelectedNavigationScreen(navigationScreen);
        }
    }

    public interface NavigationListener {
        void onNavigationItemSelected(NavigationScreen navigationScreen);
    }

    private class NavigationDividerDecoration extends DividerDecoration {
        public NavigationDividerDecoration(@NonNull Context context, int padding) {
            super(context, 0, padding, 0, padding, DRAW_DIVIDER_MIDDLE);
        }

        @Override protected boolean isDrawDividerBottom(int position, int itemCount, View view, RecyclerView parent) {
            return isCurrentHeader(position) || isNextSettings(position, itemCount);
        }

        @Override protected void applyOffsetBottom(Rect outRect, int drawableSize, int paddingTop, int paddingBottom, int position, int itemCount) {
            if (isCurrentHeader(position)) {
                super.applyOffsetBottom(outRect, drawableSize, paddingTop, 0, position, itemCount);
                return;
            }
            super.applyOffsetBottom(outRect, drawableSize, paddingTop, paddingBottom, position, itemCount);
        }

        private boolean isCurrentHeader(int position) {
            return adapter.getItem(position).getNavigationScreen() == NavigationScreen.User;
        }

        private boolean isNextSettings(int position, int itemCount) {
            return position < itemCount - 1 && adapter.getItem(position + 1).getNavigationScreen() == NavigationScreen.Settings;
        }
    }
}
