package com.code44.finance.ui.settings.about;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.code44.finance.R;
import com.code44.finance.adapters.AboutAdapter;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.LayoutType;

import javax.inject.Inject;

public class AboutActivity extends BaseActivity {
    @Inject LayoutType layoutType;

    public static void start(Context context) {
        start(context, makeIntent(context, AboutActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbarHelper.setTitle(R.string.about);

        // Get views
        final ViewPager pager_VP = (ViewPager) findViewById(R.id.pager_VP);
        final PagerSlidingTabStrip tabs_PSTS = (PagerSlidingTabStrip) findViewById(R.id.tabs_PSTS);

        // Setup
        pager_VP.setAdapter(new AboutAdapter(getFragmentManager(), this));
        pager_VP.setPageMargin(getResources().getDimensionPixelSize(R.dimen.divider));
        pager_VP.setPageMarginDrawable(new ColorDrawable(getResources().getColor(R.color.divider)));
        tabs_PSTS.setShouldExpand(layoutType.isDefault() && layoutType.isPortrait());
        tabs_PSTS.setViewPager(pager_VP);
    }
}