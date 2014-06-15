package com.code44.finance.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.code44.finance.App;
import com.code44.finance.data.providers.ProviderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class DataStore {

    private DataStore() {
    }

    public static DataStoreInsert insert(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be null.");
        }

        return new DataStoreInsert(App.getAppContext(), uri);
    }

    public static DataStoreBulkInsert bulkInsert(Uri uri) {
        if (uri == null) {
            throw new NullPointerException("Uri cannot be null.");
        }

        return new DataStoreBulkInsert(App.getAppContext(), uri);
    }

    public DataStore update(String selection, String... selectionArgs) {
        final ContentValues[] valuesArray = getValuesArray();
        for (ContentValues values : valuesArray) {
            context.getContentResolver().update(uri, values, selection, selectionArgs);
        }

        return clear();
    }

    public DataStore delete(String selection, String... selectionArgs) {
        context.getContentResolver().delete(ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, "delete"), selection, selectionArgs);

        return clear();
    }

    public DataStore undoDelete() {
        context.getContentResolver().delete(uri, selection, selectionArgs);

        return clear();
    }

    public DataStore commitDelete() {
        context.getContentResolver().delete(uri, selection, selectionArgs);

        return clear();
    }

    public static final class DataStoreInsert {
        private final Context context;
        private final Uri uri;
        private ContentValues values;

        private DataStoreInsert(Context context, Uri uri) {
            this.context = context;
            this.uri = uri;
        }

        public DataStoreInsert values(ContentValues values) {
            if (this.values != null) {
                throw new IllegalStateException("Values is already set.");
            }

            if (values == null) {
                throw new NullPointerException("Values cannot be null.");
            }

            this.values = values;
            return this;
        }

        public void execute() {
            if (values == null) {
                throw new IllegalStateException("Values must be set before executing insert.");
            }

            context.getContentResolver().insert(uri, values);
        }
    }

    public static final class DataStoreBulkInsert {
        private final Context context;
        private final Uri uri;
        private final List<ContentValues> valuesList;

        private DataStoreBulkInsert(Context context, Uri uri) {
            this.context = context;
            this.uri = uri;
            this.valuesList = new ArrayList<>();
        }

        public DataStoreBulkInsert values(ContentValues values) {
            if (values == null) {
                throw new NullPointerException("Values cannot be null.");
            }

            valuesList.add(values);
            return this;
        }

        public DataStoreBulkInsert values(ContentValues... valuesArray) {
            if (valuesArray == null || valuesArray.length == 0) {
                throw new IllegalArgumentException("Values array cannot be empty.");
            }

            values(Arrays.asList(valuesArray));
            return this;
        }

        public DataStoreBulkInsert values(Collection<ContentValues> valuesCollection) {
            if (valuesCollection == null || valuesCollection.isEmpty()) {
                throw new IllegalArgumentException("Values collection cannot be empty.");
            }

            valuesList.addAll(valuesCollection);
            return this;
        }

        public void execute() {
            ContentValues[] valuesArray = getValuesArray();
            if (valuesArray.length == 0) {
                throw new IllegalStateException("Must have at least one ContentValues before executing bulk insert.");
            }

            context.getContentResolver().bulkInsert(uri, valuesArray);
        }

        private ContentValues[] getValuesArray() {
            return valuesList.toArray(new ContentValues[valuesList.size()]);
        }
    }
}
