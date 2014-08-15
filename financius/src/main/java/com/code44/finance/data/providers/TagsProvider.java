package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

import java.util.List;
import java.util.Map;

public class TagsProvider extends BaseModelProvider {
    public static Uri uriTags() {
        return uriModels(TagsProvider.class, Tables.Tags.TABLE_NAME);
    }

    public static Uri uriTag(String tagServerId) {
        return uriModel(TagsProvider.class, Tables.Tags.TABLE_NAME, tagServerId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Tags.TABLE_NAME;
    }

    @Override
    protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override
    protected Column getServerIdColumn() {
        return Tables.Tags.SERVER_ID;
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeUpdateItems(uri, values, selection, selectionArgs, outExtras);
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, modelState, outExtras);

        final List<String> affectedIds = getIdList(getServerIdColumn(), selection, selectionArgs);
        outExtras.put("affectedIds", affectedIds);
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, modelState, extras);

        //noinspection unchecked
        final List<String> affectedIds = (List<String>) extras.get("affectedIds");
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromItemState(TransactionsProvider.uriTransactions(), modelState);

            // TODO Delete transactions for deleted tags
//            final Query query = Query.create().selectionInClause(Tables.Transactions.CATEGORY_ID.getName(), affectedIds);
//            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
        }
    }
}
