package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Column;
import com.code44.finance.data.db.Tables;

import java.util.List;
import java.util.Map;

public class CategoriesProvider extends BaseModelProvider {
    public static Uri uriCategories() {
        return uriModels(CategoriesProvider.class, Tables.Categories.TABLE_NAME);
    }

    public static Uri uriCategory(String categoryServerId) {
        return uriModel(CategoriesProvider.class, Tables.Categories.TABLE_NAME, categoryServerId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Categories.TABLE_NAME;
    }

    @Override
    protected String getQueryTables(Uri uri) {
        return getModelTable();
    }

    @Override
    protected Column getIdColumn() {
        return Tables.Categories.ID;
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeUpdateItems(uri, values, selection, selectionArgs, outExtras);
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, modelState, outExtras);

        final List<String> affectedIds = getIdList(getIdColumn(), selection, selectionArgs);
        outExtras.put("affectedIds", affectedIds);
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, ModelState modelState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, modelState, extras);

        //noinspection unchecked
        final List<String> affectedIds = (List<String>) extras.get("affectedIds");
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromItemState(TransactionsProvider.uriTransactions(), modelState);

            final Query query = Query.create().selectionInClause(Tables.Transactions.CATEGORY_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
        }
    }
}
