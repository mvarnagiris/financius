package com.code44.finance.ui;

import android.os.Bundle;

import com.code44.finance.ui.transactions.TransactionsFragment;


public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, TransactionsFragment.newInstance()).commit();
        }
    }
}
