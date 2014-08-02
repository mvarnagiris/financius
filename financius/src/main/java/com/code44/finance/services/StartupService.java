package com.code44.finance.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.api.User;
import com.code44.finance.api.currencies.CurrenciesAsyncApi;
import com.code44.finance.api.financius.FinanciusApi;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.IOUtils;

public class StartupService extends IntentService {
    public StartupService() {
        super(StartupService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        undoUncommittedDeletes();
        updateCurrenciesIfNecessary();

        if (User.get().isPremium()) {
            FinanciusApi.get().sync();
        }
    }

    private void undoUncommittedDeletes() {
        // This is necessary, because while DeleteFragment is visible, the app can terminate and we would need to handle
        // uncommitted deletes.
        // TODO Undo
    }

    private void updateCurrenciesIfNecessary() {
        final GeneralPrefs generalPrefs = GeneralPrefs.get();
        if (!generalPrefs.isAutoUpdateCurrencies() && DateUtils.isToday(generalPrefs.getAutoUpdateCurrenciesTimestamp())) {
            return;
        }

        final Cursor cursor = Query.create()
                .projection(Tables.Currencies.CODE.getName())
                .selection(Tables.Currencies.MODEL_STATE + "=?", String.valueOf(ModelState.NORMAL.asInt()))
                .from(getApplicationContext(), CurrenciesProvider.uriCurrencies())
                .execute();

        if (cursor.moveToFirst()) {
            final CurrenciesAsyncApi api = CurrenciesAsyncApi.get();
            final int iCode = cursor.getColumnIndex(Tables.Currencies.CODE.getName());
            do {
                api.updateExchangeRate(cursor.getString(iCode));
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);

        generalPrefs.setAutoUpdateCurrenciesTimestamp(System.currentTimeMillis());
    }
}
