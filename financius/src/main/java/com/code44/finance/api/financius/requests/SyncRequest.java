package com.code44.finance.api.financius.requests;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.api.BaseRequest;
import com.code44.finance.api.BaseRequestEvent;
import com.code44.finance.api.User;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.Currency;
import com.code44.finance.data.db.model.SyncState;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;

public class SyncRequest extends FinanciusBaseRequest<Void> {
    public SyncRequest(Context context, User user) {
        super(null, context, user);
    }

    @Override
    protected Void performRequest() throws Exception {
        final SQLiteDatabase database = DBHelper.get(context).getWritableDatabase();
        pushCurrencies(database);
        getCurrencies();
        return null;
    }

    @Override
    protected BaseRequestEvent<Void, ? extends BaseRequest<Void>> createEvent(Void result, Exception error, BaseRequestEvent.State state) {
        return new SyncRequestEvent(this, result, error, state);
    }

    private void pushCurrencies(SQLiteDatabase database) throws Exception {
        markInProgress(database, Tables.Currencies.SYNC_STATE);

        final Cursor cursor = Query.create()
                .projectionId(Tables.Currencies.ID)
                .projection(Tables.Currencies.PROJECTION)
                .selection(Tables.Currencies.SYNC_STATE + "=?", SyncState.IN_PROGRESS.asString())
                .from(context, CurrenciesProvider.uriCurrencies())
                .execute();
        final List<Currency> currencies = new ArrayList<>();
        do {
            currencies.add(Currency.from(cursor));
        } while (cursor.moveToNext());
        IOUtils.closeQuietly(cursor);

        new SaveCurrenciesRequest(context, User.get(), currencies).call();

        markSynced(database, Tables.Currencies.SYNC_STATE);
    }

    private void getCurrencies() throws Exception {
        new GetCurrenciesRequest(context, user).call();
    }

    private void markInProgress(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.IN_PROGRESS.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn.getName() + "<>?", SyncState.SYNCED.asString())
                .into(database, syncStateColumn.getTableName());
    }

    private void markSynced(SQLiteDatabase database, Column syncStateColumn) {
        final ContentValues values = new ContentValues();
        values.put(syncStateColumn.getName(), SyncState.SYNCED.asInt());
        DataStore.update()
                .values(values)
                .withSelection(syncStateColumn + "=?", SyncState.IN_PROGRESS.asString())
                .into(database, syncStateColumn.getTableName());
    }

    public static class SyncRequestEvent extends BaseRequestEvent<Void, SyncRequest> {
        protected SyncRequestEvent(SyncRequest request, Void result, Exception error, State state) {
            super(request, result, error, state);
        }
    }
}
