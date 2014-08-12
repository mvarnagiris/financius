package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.App;
import com.code44.finance.api.Api;
import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.api.currencies.CurrenciesApi;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.IOUtils;

public class StartupService extends IntentService {
    private final User user = User.get();
    private final GcmRegistration gcmRegistration = GcmRegistration.get();
    private final Api api = Api.get();
    private final CurrenciesApi currenciesApi = CurrenciesApi.get();
    private final GeneralPrefs generalPrefs = GeneralPrefs.get();

    public StartupService() {
        super(StartupService.class.getSimpleName());
    }

    public static void start(Context context) {
        context.startService(new Intent(context, StartupService.class));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        undoUncommittedDeletes();
        updateCurrenciesIfNecessary();

        if (user.isPremium()) {
            api.sync();

            if (!gcmRegistration.isRegisteredWithServer()) {
                api.registerDevice();
            }
        }
    }

    private void undoUncommittedDeletes() {
        // This is necessary, because while DeleteFragment is visible, the app can terminate and we would need to handle
        // uncommitted deletes.
        // TODO Undo
    }

    private void updateCurrenciesIfNecessary() {
        if (!generalPrefs.isAutoUpdateCurrencies() && DateUtils.isToday(generalPrefs.getAutoUpdateCurrenciesTimestamp())) {
            return;
        }

        final Cursor cursor = Query.create()
                .projection(Tables.Currencies.CODE.getName())
                .selection(Tables.Currencies.MODEL_STATE + "=?", String.valueOf(ModelState.NORMAL.asInt()))
                .from(App.getContext(), CurrenciesProvider.uriCurrencies())
                .execute();

        if (cursor.moveToFirst()) {
            final int iCode = cursor.getColumnIndex(Tables.Currencies.CODE.getName());
            do {
                currenciesApi.updateExchangeRate(cursor.getString(iCode));
            } while (cursor.moveToNext());
        }
        IOUtils.closeQuietly(cursor);

        generalPrefs.setAutoUpdateCurrenciesTimestamp(System.currentTimeMillis());
    }
}
