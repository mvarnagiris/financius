package com.code44.finance.api.requests;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.api.User;
import com.code44.finance.backend.endpoint.accounts.Accounts;
import com.code44.finance.backend.endpoint.accounts.model.AccountEntity;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.data.model.Model;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAccountsRequest extends GetRequest<AccountEntity> {
    private final Accounts accountsService;
    private final Map<String, CurrencyFormat> currencies;

    public GetAccountsRequest(Context context, User user, Accounts accountsService) {
        super(null, context, user);
        Preconditions.notNull(accountsService, "Accounts cannot be null.");

        this.accountsService = accountsService;
        currencies = new HashMap<>();
    }

    @Override protected long getLastTimestamp(User user) {
        return user.getAccountsTimestamp();
    }

    @Override protected List<AccountEntity> performRequest(long timestamp) throws Exception {
        return accountsService.list(timestamp).execute().getItems();
    }

    @Override protected Model getModelFrom(AccountEntity entity) {
        return null;
    }

    @Override protected void saveNewTimestamp(User user, long newTimestamp) {
        user.setAccountsTimestamp(newTimestamp);
    }

    @Override protected Uri getSaveUri() {
        return AccountsProvider.uriAccounts();
    }

    private CurrencyFormat getCurrencyFor(AccountEntity entity) {
        CurrencyFormat currencyFormat = currencies.get(entity.getCurrencyId());

        if (currencyFormat == null) {
            final Cursor cursor = Query.create()
                    .projectionLocalId(Tables.CurrencyFormats.LOCAL_ID)
                    .projection(Tables.CurrencyFormats.PROJECTION)
                    .selection(Tables.CurrencyFormats.ID + "=?", entity.getCurrencyId())
                    .from(getContext(), CurrenciesProvider.uriCurrencies())
                    .execute();
            currencyFormat = CurrencyFormat.from(cursor);
            IOUtils.closeQuietly(cursor);
            currencies.put(entity.getCurrencyId(), currencyFormat);
        }

        return currencyFormat;
    }
}
