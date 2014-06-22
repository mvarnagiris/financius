package com.code44.finance.ui;

import android.database.Cursor;
import android.os.Bundle;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;


public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, OverviewFragment.newInstance()).commit();
        }

        Cursor cursor = Query.get().projectionId(Tables.Transactions.ID)
                .projection(Tables.Transactions.PROJECTION)
                .projection(Tables.Categories.PROJECTION).from(this, TransactionsProvider.uriTransactions()).execute();
        cursor.moveToFirst();
        int count = cursor.getCount();
        IOUtils.closeQuietly(cursor);
    }
}
