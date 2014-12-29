package com.code44.finance.adapters;

import android.content.Context;

import com.code44.finance.R;


public class NavigationBottomAdapter extends NavigationAdapter {

    public NavigationBottomAdapter(Context context) {
        super(context);

        items.clear();
        items.add(new NavigationAdapter.NavigationItem(NavigationAdapter.NavigationScreen.Settings, context.getString(R.string.settings), R.drawable.ic_settings_black_24dp));
        items.add(new NavigationAdapter.NavigationItem(NavigationAdapter.NavigationScreen.About, context.getString(R.string.about), R.drawable.ic_info_outline_black_24dp));
    }
}
