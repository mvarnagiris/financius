package com.code44.finance.data;

import android.content.ContentValues;
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

    public static DataStoreInsert insert() {
        return new DataStoreInsert();
    }

    public static DataStoreUpdate update() {
        return new DataStoreUpdate();
    }

    public static DataStoreDelete delete() {
        return new DataStoreDelete();
    }

    public static DataStoreUndoDelete undoDelete() {
        return new DataStoreUndoDelete();
    }

    public static DataStoreCommitDelete commitDelete() {
        return new DataStoreCommitDelete();
    }

    public static DataStoreBulkInsert bulkInsert() {
        return new DataStoreBulkInsert();
    }

    public static final class DataStoreInsert {
        private ContentValues values;

        private DataStoreInsert() {
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

        public void into(Uri uri) {
            if (values == null) {
                throw new IllegalStateException("Values must be set before executing insert.");
            }

            App.getAppContext().getContentResolver().insert(uri, values);
        }
    }

    public static final class DataStoreUpdate {
        private ContentValues values;
        private String selection;
        private String[] selectionArgs;

        private DataStoreUpdate() {
        }

        public DataStoreUpdate values(ContentValues values) {
            if (this.values != null) {
                throw new IllegalStateException("Values is already set.");
            }

            if (values == null) {
                throw new NullPointerException("Values cannot be null.");
            }

            this.values = values;
            return this;
        }

        public DataStoreUpdate withSelection(String selection, String... selectionArgs) {
            this.selection = selection;
            this.selectionArgs = selectionArgs;

            return this;
        }

        public void from(Uri uri) {
            if (values == null) {
                throw new IllegalStateException("Values must be set before executing insert.");
            }

            App.getAppContext().getContentResolver().update(uri, values, selection, selectionArgs);
        }
    }

    public static final class DataStoreDelete {
        private String selection;
        private String[] selectionArgs;

        private DataStoreDelete() {
        }

        public DataStoreDelete selection(String selection, String... selectionArgs) {
            this.selection = selection;
            this.selectionArgs = selectionArgs;

            return this;
        }

        public void from(Uri uri) {
            App.getAppContext().getContentResolver().delete(ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, "delete"), selection, selectionArgs);
        }
    }

    public static final class DataStoreUndoDelete {
        private DataStoreUndoDelete() {
        }

        public void from(Uri uri) {
            App.getAppContext().getContentResolver().delete(ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, "undo"), null, null);
        }
    }

    public static final class DataStoreCommitDelete {
        private DataStoreCommitDelete() {
        }

        public void from(Uri uri) {
            App.getAppContext().getContentResolver().delete(ProviderUtils.withQueryParameter(uri, ProviderUtils.QueryParameterKey.DELETE_MODE, "commit"), null, null);
        }
    }

    public static final class DataStoreBulkInsert {
        private final List<ContentValues> valuesList;

        private DataStoreBulkInsert() {
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

        public void into(Uri uri) {
            ContentValues[] valuesArray = getValuesArray();
            if (valuesArray.length == 0) {
                throw new IllegalStateException("Must have at least one ContentValues before executing bulk insert.");
            }

            App.getAppContext().getContentResolver().bulkInsert(uri, valuesArray);
        }

        private ContentValues[] getValuesArray() {
            return valuesList.toArray(new ContentValues[valuesList.size()]);
        }
    }
}
