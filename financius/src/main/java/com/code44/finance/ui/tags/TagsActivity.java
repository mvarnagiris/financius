package com.code44.finance.ui.tags;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.code44.finance.R;
import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.ModelListActivityOld;
import com.code44.finance.ui.ModelListFragment;

import java.util.List;

public class TagsActivity extends ModelListActivityOld {
    public static void start(Context context) {
        startActivity(context, makeIntentView(context, TagsActivity.class));
    }

    public static void startSelect(Fragment fragment, int requestCode) {
        startActivityForResult(fragment, makeIntentSelect(fragment.getActivity(), TagsActivity.class), requestCode);
    }

    public static void startMultiSelect(Fragment fragment, int requestCode, List<Tag> selectedTags) {
        startActivityForResult(fragment, makeIntentMultiSelect(fragment.getActivity(), TagsActivity.class, selectedTags), requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode, Parcelable[] selectedModels) {
        return TagsFragment.newInstance(mode, selectedModels);
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return null;
    }
}
