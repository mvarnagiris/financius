package com.code44.finance.api.endpoints.requests.currencies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.SendEntitiesRequest;
import com.code44.finance.backend.financius.model.CurrenciesBody;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.EventBus;

public class SendCurrenciesRequest extends SendEntitiesRequest<CurrencyFormat, CurrenciesBody> {
    public SendCurrenciesRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device) {
        super(eventBus, endpointFactory, context, user, dbHelper, device);
    }

    @Override protected Query getQuery() {
        return Tables.CurrencyFormats.getQuery();
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.CurrencyFormats.SYNC_STATE;
    }

    @Override protected Uri getUri() {
        return CurrenciesProvider.uriCurrencies();
    }

    @Override protected CurrencyFormat getModel(Cursor cursor) {
        return CurrencyFormat.from(cursor);
    }

    @Override protected CurrenciesBody createBody() {
        return new CurrenciesBody();
    }

    @Override protected long performRequest(CurrenciesBody body) throws Exception {
        return getEndpoint().updateCurrencies(body).execute().getUpdateTimestamp();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastCurrenciesUpdateTimestamp(lastUpdateTimestamp);
    }
}
