package com.code44.finance.ui.categories.edit;

import android.support.annotation.NonNull;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.ThemeUtils;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

class CategoryEditValidator implements ModelEditActivity.ModelEditValidator<CategoryEditData> {
    private final EditText titleEditText;

    public CategoryEditValidator(@NonNull EditText titleEditText) {
        this.titleEditText = checkNotNull(titleEditText, "Title EditText cannot be null.");
    }

    @Override public boolean validate(@NonNull CategoryEditData modelEditData, boolean showError) {
        boolean isValid = true;

        if (Strings.isNullOrEmpty(modelEditData.getTitle())) {
            isValid = false;
            if (showError) {
                titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.textColorNegative));
            }
        }

        return isValid;
    }
}
