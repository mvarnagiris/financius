package com.code44.finance.ui.currencies.edit;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.activities.ModelEditActivity;
import com.google.common.base.Strings;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

class CurrencyFormatEditValidator implements ModelEditActivity.ModelEditValidator<CurrencyFormatEditData>, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_CURRENCIES = 5121;

    private final TextView errorTextView;
    private final boolean isNewModel;
    private final Set<String> existingCurrencyCodes = new HashSet<>();

    public CurrencyFormatEditValidator(@NonNull BaseActivity activity, @NonNull TextView errorTextView, boolean isNewModel) {
        checkNotNull(activity, "Activity cannot be null.");
        this.errorTextView = checkNotNull(errorTextView, "Error TextView cannot be null.");
        this.isNewModel = isNewModel;

        if (isNewModel) {
            activity.getSupportLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
        }
    }

    @Override public boolean validate(@NonNull CurrencyFormatEditData modelEditData, boolean showError) {
        boolean isValid = true;

        final String code = modelEditData.getCode();
        if (!Strings.isNullOrEmpty(code) && isCurrencyDuplicate(code)) {
            isValid = false;
            if (showError) {
                errorTextView.setText(errorTextView.getContext().getString(R.string.l_currency_exists));
                errorTextView.setVisibility(View.VISIBLE);
            }
        } else {
            errorTextView.setVisibility(View.GONE);
        }

        if (Strings.isNullOrEmpty(code) || code.length() != 3) {
            isValid = false;
            if (showError) {
                errorTextView.setText(R.string.l_please_enter_currency_code);
                errorTextView.setVisibility(View.VISIBLE);
            }
        }

        return isValid;
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CURRENCIES) {
            return Tables.CurrencyFormats.getQuery().asCursorLoader(errorTextView.getContext(), CurrenciesProvider.uriCurrencies());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CURRENCIES) {
            existingCurrencyCodes.clear();
            if (data.moveToFirst()) {
                do {
                    existingCurrencyCodes.add(CurrencyFormat.from(data).getCode());
                } while (data.moveToNext());
            }
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> loader) {

    }

    public boolean isCurrencyDuplicate(String code) {
        return existingCurrencyCodes.contains(code.toUpperCase()) && isNewModel;
    }
}
