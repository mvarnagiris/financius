package com.code44.finance.api.currencies;

import android.database.Cursor;

import com.code44.finance.api.Request;
import com.code44.finance.common.utils.StringUtils;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import retrofit.client.Response;

public class CurrencyRequest extends Request {
    private final String fromCode;
    private final String toCode;
    @Inject CurrenciesRequestService requestService;

    public CurrencyRequest(String fromCode, String toCode) {
        this.fromCode = fromCode;
        this.toCode = toCode;
    }

    @Override
    protected void performRequest() throws Exception {
        final Response rawResponse = requestService.getExchangeRate(fromCode, toCode);
        final JsonObject json = IOUtils.readJsonObject(rawResponse);
        final double exchangeRate = json.get("rate").getAsDouble();

        final Cursor cursor = Query.create()
                .projection(Tables.Currencies.ID.getName())
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.CODE + "=?", fromCode)
                .from(CurrenciesProvider.uriCurrencies())
                .execute();
        final Currency currency = Currency.from(cursor);
        IOUtils.closeQuietly(cursor);

        currency.setExchangeRate(exchangeRate);
        if (!StringUtils.isEmpty(currency.getServerId())) {
            DataStore.bulkInsert().values(currency.asContentValues()).into(CurrenciesProvider.uriCurrencies());
        }
    }
}
