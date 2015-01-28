package com.code44.finance.ui.transactions.edit.presenters;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.presenters.Presenter;

public class FlagsPresenter extends Presenter {
    private final CheckBox includeInReportsCheckBox;
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    public FlagsPresenter(BaseActivity activity, CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        includeInReportsCheckBox = findView(activity, R.id.includeInReportsCheckBox);

        onCheckedChangeListener = checkedChangeListener;
        includeInReportsCheckBox.setOnCheckedChangeListener(checkedChangeListener);
    }

    public void setIncludeInReports(boolean includeInReports) {
        includeInReportsCheckBox.setOnCheckedChangeListener(null);
        includeInReportsCheckBox.setChecked(includeInReports);
        includeInReportsCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }
}
