package com.code44.finance.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.ui.categories.CategoriesFragment;

public class CategoriesPagerAdapter extends FragmentPagerAdapter {
    private final Context context;

    public CategoriesPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return CategoriesFragment.newInstance(ModelListFragment.Mode.VIEW, TransactionType.Expense);
        } else {
            return CategoriesFragment.newInstance(ModelListFragment.Mode.VIEW, TransactionType.Income);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.expense);
        } else {
            return context.getString(R.string.income);
        }
    }
}
