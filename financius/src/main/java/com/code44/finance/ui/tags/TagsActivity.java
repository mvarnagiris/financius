package com.code44.finance.ui.tags;

import android.app.Fragment;
import android.content.Context;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.ui.OnModelListActivity;

public class TagsActivity extends OnModelListActivity {
    public static void start(Context context) {
        start(context, makeIntentView(context, TagsActivity.class));
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startForResult(fragment, makeIntentSelect(fragment.getActivity(), TagsActivity.class), requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.tags;
    }

    @Override
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode) {
        return TagsFragment.newInstance(mode);
    }
}
