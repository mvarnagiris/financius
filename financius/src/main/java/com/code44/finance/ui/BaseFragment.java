package com.code44.finance.ui;


import android.app.Fragment;

import com.code44.finance.R;

public class BaseFragment extends Fragment {
    public String getTitle() {
        return getString(R.string.app_name);
    }
}
