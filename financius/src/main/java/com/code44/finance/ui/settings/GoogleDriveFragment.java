package com.code44.finance.ui.settings;

import android.os.Bundle;

import com.code44.finance.ui.BaseFragment;

public class GoogleDriveFragment extends BaseFragment {
    private static final String ARG_GOOGLE_API_UNIQUE_ID = "ARG_GOOGLE_API_UNIQUE_ID";

    private String googleApiUniqueId;

    public static GoogleDriveFragment newInstance(String googleApiUniqueId) {
        final Bundle args = new Bundle();
        args.putString(ARG_GOOGLE_API_UNIQUE_ID, googleApiUniqueId);

        final GoogleDriveFragment fragment = new GoogleDriveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        googleApiUniqueId = getArguments().getString(ARG_GOOGLE_API_UNIQUE_ID, "");
    }
}
