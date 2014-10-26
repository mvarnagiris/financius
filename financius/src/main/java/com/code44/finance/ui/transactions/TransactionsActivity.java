package com.code44.finance.ui.transactions;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.code44.finance.adapters.NavigationAdapter;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;
import com.code44.finance.utils.analytics.Analytics;

public class TransactionsActivity extends ModelListActivity {
    public static Intent makeIntentView(Context context) {
        return makeIntentView(context, TransactionsActivity.class);
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
