package com.code44.finance.ui.currencies;

import android.os.Bundle;

import com.code44.finance.ui.BaseModelFragment;

public class CurrencyFragment extends BaseModelFragment {
    public static CurrencyFragment newInstance(long currencyId) {
        final Bundle args = makeArgs(currencyId);

        final CurrencyFragment fragment = new CurrencyFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
