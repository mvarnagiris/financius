package com.code44.finance.ui;

import android.os.Bundle;

public class BaseModelFragment extends BaseFragment {
    protected static final String ARG_ITEM_ID = "ITEM_ID";

    public static Bundle makeArgs(long itemId) {
        final Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        return args;
    }
}
