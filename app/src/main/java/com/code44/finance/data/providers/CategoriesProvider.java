package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

import java.util.List;
import java.util.Map;

public class CategoriesProvider extends ModelProvider {
    public static Uri uriCategories() {
        return uriModels(CategoriesProvider.class, Tables.Categories.TABLE_NAME);
    }

    public static Uri uriCategory(String categoryServerId) {
        return uriModel(CategoriesProvider.class, Tables.Categories.TABLE_NAME, categoryServerId);
    }

    @Override protected String getModelTable() {
        return Tables.Categories.TABLE_NAME;
    }

    @Override protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override protected Column getIdColumn() {
        return Tables.Categories.ID;
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

            final Query query = Query.create()
                    .selection(Tables.Transactions.MODEL_STATE + "<>? and ", ModelState.Deleted.asString())
                    .selectionInClause(Tables.Transactions.CATEGORY_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
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
