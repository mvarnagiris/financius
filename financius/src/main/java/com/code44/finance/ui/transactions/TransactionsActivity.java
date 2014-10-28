package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.ModelListActivityOld;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.analytics.Analytics;

public class TransactionsActivity extends ModelListActivityOld {
    public static Intent makeIntentView(Context context) {
        return makeIntentView(context, TransactionsActivity.class);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setShowDrawer(true);
        setShowDrawerToggle(true);
        super.onCreate(savedInstanceState);
    }

    @Override protected ModelListFragment createModelsFragment(ModelListFragment.Mode mode, Parcelable[] selectedModels) {
        return TransactionsFragment.newInstance();
    }

    @Override protected NavigationAdapter.NavigationScreen getNavigationScreen() {
        return NavigationAdapter.NavigationScreen.Transactions;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.TransactionList;
    }
}
