package com.code44.finance.ui.tags.edit;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.ThemeUtils;

import static com.google.common.base.Preconditions.checkNotNull;

class TagEditValidator implements ModelEditActivity.ModelEditValidator<TagEditData> {
    private final EditText titleEditText;

    public TagEditValidator(@NonNull EditText titleEditText) {
        this.titleEditText = checkNotNull(titleEditText, "Title EditText cannot be null.");
    }

    @Override public boolean validate(@NonNull TagEditData modelEditData, boolean showError) {
        boolean isValid = true;

        if (TextUtils.isEmpty(modelEditData.getTitle())) {
            isValid = false;
            if (showError) {
                titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.textColorNegative));
            }
        }

        return isValid;
    }
}
