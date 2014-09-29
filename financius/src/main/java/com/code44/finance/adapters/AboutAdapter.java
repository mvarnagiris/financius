package com.code44.finance.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.code44.finance.R;
import com.code44.finance.ui.settings.about.ChangeLogFragment;
import com.code44.finance.ui.settings.about.FaqFragment;

public class AboutAdapter extends FragmentPagerAdapter {
    private final Context context;

    public AboutAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ChangeLogFragment.newInstance();
            case 1:
                return FaqFragment.newInstance();
        }
        return null;
    }

    @Override public int getCount() {
        return 2;
    }

    @Override public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.change_log);
            case 1:
                return context.getString(R.string.faq);
        }
        return super.getPageTitle(position);
    }
}
