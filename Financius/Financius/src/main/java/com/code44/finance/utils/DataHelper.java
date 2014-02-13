package com.code44.finance.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import com.code44.finance.App;
import com.code44.finance.providers.BaseItemsProvider;

public class DataHelper
{
    private static ContentResolver getContentResolver()
    {
        return App.getAppContext().getContentResolver();
    }

    public static void create(Uri uri, ContentValues values)
    {
        create(uri, values, null);
    }

    public static void create(Uri uri, ContentValues values, ValuesUpdater valuesUpdater)
    {
        new CreateAsyncTask(uri, values, valuesUpdater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void update(Uri uri, long itemId, ContentValues values)
    {
        update(uri, itemId, values, null);
    }

    public static void update(Uri uri, long itemId, ContentValues values, ValuesUpdater valuesUpdater)
    {
        new UpdateAsyncTask(uri, itemId, values, valuesUpdater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public static void delete(Uri uri, long[] itemIDs)
    {
        new DeleteAsyncTask(uri, itemIDs).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class CreateAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private final Uri uri;
        private final ContentValues values;
        private final ValuesUpdater valuesUpdater;

        protected CreateAsyncTask(Uri uri, ContentValues values, ValuesUpdater valuesUpdater)
        {
            this.uri = uri;
            this.values = values;
            this.valuesUpdater = valuesUpdater;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            if (valuesUpdater != null)
                valuesUpdater.updateValues(values);
            getContentResolver().insert(uri, values);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private final Uri uri;
        private final long itemId;
        private final ContentValues values;
        private final ValuesUpdater valuesUpdater;

        protected UpdateAsyncTask(Uri uri, long itemId, ContentValues values, ValuesUpdater valuesUpdater)
        {
            this.uri = uri;
            this.itemId = itemId;
            this.values = values;
            this.valuesUpdater = valuesUpdater;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            if (valuesUpdater != null)
                valuesUpdater.updateValues(values);
            values.put(BaseColumns._ID, itemId);
            getContentResolver().update(uri, values, BaseColumns._ID + "=?", new String[]{String.valueOf(itemId)});
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Void, Void, Void>
    {
        private final Uri uri;
        private final long[] itemIDs;

        protected DeleteAsyncTask(Uri uri, long[] itemIDs)
        {
            this.uri = uri;
            this.itemIDs = itemIDs;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            final BaseItemsProvider.InClause inClause = BaseItemsProvider.InClause.getInClause(itemIDs, BaseColumns._ID);
            getContentResolver().delete(uri, inClause.getSelection(), inClause.getSelectionArgs());
            return null;
        }
    }

    public static interface ValuesUpdater
    {
        public void updateValues(ContentValues values);
    }
}