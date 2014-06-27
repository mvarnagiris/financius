package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.code44.finance.R;
import com.code44.finance.adapters.CategoriesPagerAdapter;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.LayoutType;

public class CategoriesActivity extends ModelListActivity {

    public static void start(Context context, View expandFrom) {
        final Intent intent = makeIntentView(context, CategoriesActivity.class);
        startScaleUp(context, intent, expandFrom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get views
        final PagerSlidingTabStrip tabs_PSTS = (PagerSlidingTabStrip) findViewById(R.id.tabs_PSTS);
        final ViewPager pager_VP = (ViewPager) findViewById(R.id.pager_VP);

        // Setup
        int underlineColor = getResources().getColor(R.color.divider);
        underlineColor = Color.argb(128, Color.red(underlineColor), Color.green(underlineColor), Color.blue(underlineColor));
        final CategoriesPagerAdapter adapter = new CategoriesPagerAdapter(this, getSupportFragmentManager());
        pager_VP.setAdapter(adapter);
        pager_VP.setPageMargin(tabs_PSTS.getUnderlineHeight());
        pager_VP.setPageMarginDrawable(new ColorDrawable(underlineColor));
        tabs_PSTS.setShouldExpand(LayoutType.isDefault() && LayoutType.isPortrait());
        tabs_PSTS.setIndicatorColorResource(R.color.brand_primary);
        tabs_PSTS.setIndicatorHeight(tabs_PSTS.getUnderlineHeight() * 2);
        tabs_PSTS.setUnderlineColor(underlineColor);
        //tabs_PSTS.setTabBackground(R.drawable.btn_borderless);
        tabs_PSTS.setViewPager(pager_VP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.categories;
    }

    @Override
    protected ModelListFragment createModelsFragment(int mode) {
        return CategoriesFragment.newInstance(mode, Category.Type.EXPENSE);
    }

    @Override
    protected int inflateActivity() {
        setContentView(R.layout.activity_categories);
        return 0;
    }
}
