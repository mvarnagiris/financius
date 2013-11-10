package com.code44.finance.ui.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.code44.finance.R;
import com.code44.finance.ui.AbstractFragment;

public class YourDataFragment extends AbstractFragment
{
    public static YourDataFragment newInstance()
    {
        return new YourDataFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_your_data, container, false);
    }
}