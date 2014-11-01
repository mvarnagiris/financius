package com.code44.finance.ui.tags;

import android.content.Context;

import com.code44.finance.R;
import com.code44.finance.ui.ModelEditActivityOld;
import com.code44.finance.ui.ModelFragment;

public class TagEditActivity extends ModelEditActivityOld {
    public static void start(Context context, String tagServerId) {
        startActivity(context, makeIntent(context, TagEditActivity.class, tagServerId));
    }

    @Override
    protected int getActionBarTitleResId() {
        return R.string.tag;
    }

    @Override
    protected ModelFragment createModelFragment(String modelServerId) {
        return TagEditFragment.newInstance(modelServerId);
    }
}
