package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.code44.finance.R;
import com.code44.finance.adapters.CategoriesPagerAdapter;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.LayoutType;

public class CategoriesActivity extends ModelListActivity {

    public static void start(Context context) {
        final Intent intent = makeIntentView(context, CategoriesActivity.class);
        start(context, intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get views
        final PagerSlidingTabStrip tabs_PSTS = (PagerSlidingTabStrip) findViewById(R.id.tabs_PSTS);
        final ViewPager pager_VP = (ViewPager) findViewById(R.id.pager_VP);

        // Setup
        final CategoriesPagerAdapter adapter = new CategoriesPagerAdapter(this, getFragmentManager());
        pager_VP.setAdapter(adapter);
        pager_VP.setPageMargin(getResources().getDimensionPixelSize(R.dimen.divider));
        pager_VP.setPageMarginDrawable(new ColorDrawable(getResources().getColor(R.color.divider)));
        tabs_PSTS.setShouldExpand(LayoutType.isDefault() && LayoutType.isPortrait());
        tabs_PSTS.setViewPager(pager_VP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.categories;
    }

    @Override
    protected ModelListFragment createModelsFragment(Mode mode) {
        // Fragments are created in adapter
        return null;
    }

    @Override
    protected void startModelActivity(View expandFrom, long modelId) {
        CategoryActivity.start(this, modelId);
    }

    @Override
    protected void startModelEditActivity(View expandFrom, long modelId) {
        CategoryEditActivity.start(this, modelId);
    }

    @Override
    protected int inflateActivity() {
        setContentView(R.layout.activity_categories);
        return 0;
    }
}
