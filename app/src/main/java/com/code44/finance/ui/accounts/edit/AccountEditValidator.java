package com.code44.finance.ui.accounts.edit;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.code44.finance.utils.ThemeUtils;
import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkNotNull;

class AccountEditValidator implements ModelEditActivity.ModelEditValidator<AccountEditData> {
    private final EditText titleEditText;
    private final Button currencyButton;

    public AccountEditValidator(@NonNull EditText titleEditText, @NonNull Button currencyButton) {
        this.titleEditText = checkNotNull(titleEditText, "Title EditText cannot be null.");
        this.currencyButton = checkNotNull(currencyButton, "Currency Button cannot be null.");
    }

    @Override public boolean validate(@NonNull AccountEditData modelEditData, boolean showError) {
        boolean isValid = true;

        if (Strings.isNullOrEmpty(modelEditData.getTitle())) {
            isValid = false;
            if (showError) {
                titleEditText.setHintTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.colorError));
            }
        }

        final String currencyCode = modelEditData.getCurrencyCode();
        if (TextUtils.isEmpty(currencyCode) || currencyCode.length() != 3) {
            isValid = false;
            if (showError) {
                currencyButton.setTextColor(ThemeUtils.getColor(titleEditText.getContext(), R.attr.colorError));
            }
        }

        return isValid;
    }
}
