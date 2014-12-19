package com.code44.finance.ui.transactions.controllers;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;

public class FlagsViewController extends ViewController {
    private final CheckBox includeInReportsCheckBox;
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public FlagsViewController(BaseActivity activity, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        includeInReportsCheckBox = findView(activity, R.id.includeInReportsCheckBox);

        onCheckedChangeListener = checkedChangeListener;
        includeInReportsCheckBox.setOnCheckedChangeListener(checkedChangeListener);
    }

    @Override public void showError(Throwable error) {
    }

    public void setIncludeInReports(boolean includeInReports) {
        includeInReportsCheckBox.setOnCheckedChangeListener(null);
        includeInReportsCheckBox.setChecked(includeInReports);
        includeInReportsCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
