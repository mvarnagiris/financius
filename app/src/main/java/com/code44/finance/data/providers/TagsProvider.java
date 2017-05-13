package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;
import com.code44.finance.utils.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TagsProvider extends ModelProvider {
    public static Uri uriTags() {
        return uriModels(TagsProvider.class, Tables.Tags.TABLE_NAME);
    }

    public static Uri uriTag(String tagServerId) {
        return uriModel(TagsProvider.class, Tables.Tags.TABLE_NAME, tagServerId);
    }

    @Override protected String getModelTable() {
        return Tables.Tags.TABLE_NAME;
    }

    @Override protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override protected Column getIdColumn() {
        return Tables.Tags.ID;
    }

    @Override protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, extras);

        final List<String> affectedIds = getColumnValues(extras);
        final ModelState modelState = getModelState(extras);
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromModelState(TransactionsProvider.uriTransactions(), modelState);

            final Cursor cursor = Query.create()
                    .projection(Tables.TransactionTags.TRANSACTION_ID.getName())
                    .selectionInClause(Tables.TransactionTags.TAG_ID.getName(), affectedIds)
                    .from(getDatabase(), Tables.TransactionTags.TABLE_NAME)
                    .execute();
            if (cursor.moveToFirst()) {
                final List<String> transactionIds = new ArrayList<>();
                do {
                    transactionIds.add(cursor.getString(0));
                } while (cursor.moveToNext());

                final Query query = Query.create()
                        .selection(Tables.Transactions.MODEL_STATE + "<>? and ", ModelState.Deleted.asString())
                        .selectionInClause(Tables.Transactions.ID.getName(), transactionIds);
                getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
            }
            IOUtils.closeQuietly(cursor);
        }
    }

    @Override protected Uri[] getOtherUrisToNotify() {
        return new Uri[]{TransactionsProvider.uriTransactions()};
    }

    @Override protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, outExtras);
        putColumnToExtras(outExtras, getIdColumn(), selection, selectionArgs);
    }
}
