package com.code44.finance.ui.accounts;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.ui.ModelListFragment;

public class AccountsFragment extends ModelListFragment {
    public static AccountsFragment newInstance() {
        final Bundle args = makeArgs();

        final AccountsFragment fragment = new AccountsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void startModelActivity(Context context, View expandFrom, long modelId) {

    }

    @Override
    protected void startModelEditActivity(Context context, View expandFrom, long modelId) {

    }

    @Override
    protected BaseModelsAdapter createAdapter(Context context) {
        return null;
    }

    @Override
    protected Uri getUri() {
        return null;
    }
}
