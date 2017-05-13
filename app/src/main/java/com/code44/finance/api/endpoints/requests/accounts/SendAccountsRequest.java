package com.code44.finance.api.endpoints.requests.accounts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.code44.finance.api.endpoints.Device;
import com.code44.finance.api.endpoints.EndpointFactory;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.api.endpoints.requests.SendEntitiesRequest;
import com.code44.finance.backend.financius.model.AccountsBody;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.utils.EventBus;

public class SendAccountsRequest extends SendEntitiesRequest<Account, AccountsBody> {
    public SendAccountsRequest(@NonNull EventBus eventBus, @NonNull EndpointFactory endpointFactory, @NonNull Context context, @NonNull User user, @NonNull DBHelper dbHelper, @NonNull Device device) {
        super(eventBus, endpointFactory, context, user, dbHelper, device);
    }

    @Override protected Query getQuery() {
        return Tables.Accounts.getQuery();
    }

    @Override protected Column getSyncStateColumn() {
        return Tables.Accounts.SYNC_STATE;
    }

    @Override protected Uri getUri() {
        return AccountsProvider.uriAccounts();
    }

    @Override protected Account getModel(Cursor cursor) {
        return Account.from(cursor);
    }

    @Override protected AccountsBody createBody() {
        return new AccountsBody();
    }

    @Override protected long performRequest(AccountsBody body) throws Exception {
        return getEndpoint().updateAccounts(body).execute().getUpdateTimestamp();
    }

    @Override protected void saveLastUpdateTimestamp(User user, long lastUpdateTimestamp) {
        user.setLastAccountsUpdateTimestamp(lastUpdateTimestamp);
    }
}
