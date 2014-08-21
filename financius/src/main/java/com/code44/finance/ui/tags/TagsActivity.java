package com.code44.finance.ui.tags;

import android.app.Fragment;
import android.content.Context;
import android.os.Parcelable;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

import java.util.Set;

public class TagsActivity extends ModelListActivity {
    public static void start(Context context) {
        start(context, makeIntentView(context, TagsActivity.class));
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startForResult(fragment, makeIntentSelect(fragment.getActivity(), TagsActivity.class), requestCode);
    }

    public static void startMultiSelect(Fragment fragment, int requestCode, Set<Tag> selectedTags) {
        startForResult(fragment, makeIntentMultiSelect(fragment.getActivity(), TagsActivity.class, selectedTags), requestCode);
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
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode, Parcelable[] selectedModels) {
        return TagsFragment.newInstance(mode, selectedModels);
    }
}
