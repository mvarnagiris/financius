package com.code44.finance.data.providers;

import android.content.ContentValues;
import android.net.Uri;

import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;

import java.util.List;
import java.util.Map;

public class CategoriesProvider extends BaseModelProvider {
    public static Uri uriCategories() {
        return uriModels(CategoriesProvider.class, Tables.Categories.TABLE_NAME);
    }

    public static Uri uriCategory(long categoryId) {
        return uriModel(CategoriesProvider.class, Tables.Categories.TABLE_NAME, categoryId);
    }

    @Override
    protected String getModelTable() {
        return Tables.Categories.TABLE_NAME;
    }

    @Override
    protected String getQueryTables() {
        return getModelTable();
    }

    @Override
    protected void onBeforeUpdateItems(Uri uri, ContentValues values, String selection, String[] selectionArgs, Map<String, Object> outExtras) {
        super.onBeforeUpdateItems(uri, values, selection, selectionArgs, outExtras);
        throw new IllegalArgumentException("Update is not supported.");
    }

    @Override
    protected void onBeforeDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> outExtras) {
        super.onBeforeDeleteItems(uri, selection, selectionArgs, itemState, outExtras);

        final List<Long> affectedIds = getIdList(Tables.Categories.TABLE_NAME, selection, selectionArgs);
        outExtras.put("affectedIds", affectedIds);
    }

    @Override
    protected void onAfterDeleteItems(Uri uri, String selection, String[] selectionArgs, BaseModel.ItemState itemState, Map<String, Object> extras) {
        super.onAfterDeleteItems(uri, selection, selectionArgs, itemState, extras);

        //noinspection unchecked
        final List<Long> affectedIds = (List<Long>) extras.get("affectedIds");
        if (affectedIds.size() > 0) {
            final Uri transactionsUri = uriForDeleteFromItemState(TransactionsProvider.uriTransactions(), itemState);

            final Query query = Query.get().selectionInClause(Tables.Transactions.CATEGORY_ID.getName(), affectedIds);
            getContext().getContentResolver().delete(transactionsUri, query.getSelection(), query.getSelectionArgs());
        }
    }
}
