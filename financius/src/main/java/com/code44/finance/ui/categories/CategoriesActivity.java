package com.code44.finance.ui.categories;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.astuetz.PagerSlidingTabStrip;
import com.code44.finance.R;
import com.code44.finance.adapters.CategoriesPagerAdapter;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.ui.OnModelListActivity;
import com.code44.finance.utils.LayoutType;

public class CategoriesActivity extends OnModelListActivity {
    private static final String EXTRA_CATEGORY_TYPE = "EXTRA_CATEGORY_TYPE";

    private CategoryType categoryType;

    public static void start(Context context) {
        final Intent intent = makeIntentView(context, CategoriesActivity.class);
        start(context, intent);
    }

    public static void startSelect(Fragment fragment, int requestCode, CategoryType categoryType) {
        final Intent intent = makeIntentSelect(fragment.getActivity(), CategoriesActivity.class);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        startForResult(fragment, intent, requestCode);
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
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode) {
        return mode == ModelListFragment.Mode.VIEW ? null : CategoriesFragment.newInstance(mode, categoryType);
    }

    @Override
    protected int inflateActivity() {
        if (mode == ModelListFragment.Mode.VIEW) {
            setContentView(R.layout.activity_categories);

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
            return 0;
        } else {
            return super.inflateActivity();
        }
    }

    @Override protected void readExtras() {
        super.readExtras();
        categoryType = (CategoryType) getIntent().getSerializableExtra(EXTRA_CATEGORY_TYPE);
    }
}
