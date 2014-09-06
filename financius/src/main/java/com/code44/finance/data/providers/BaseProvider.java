package com.code44.finance.data.providers;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.App;
import com.code44.finance.BuildConfig;
import com.code44.finance.data.db.DBHelper;

import javax.inject.Inject;

public abstract class BaseProvider extends ContentProvider {
    protected static final String CONTENT_URI_BASE = "content://";

    protected static final String TYPE_LIST_BASE = "vnd.android.cursor.dir/vnd.code44.";
    protected static final String TYPE_ITEM_BASE = "vnd.android.cursor.item/vnd.code44.";

    protected final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    protected SQLiteDatabase database;

    @Inject DBHelper dbHelper;

    protected static String getAuthority(Class<? extends BaseProvider> cls) {
        return BuildConfig.PACKAGE_NAME + ".data.providers." + cls.getSimpleName();
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    protected String getAuthority() {
        return getAuthority(getClass());
    }

    protected SQLiteDatabase getDatabase() {
        if (database == null) {
            App.with(getContext()).inject(this);
            database = dbHelper.getWritableDatabase();
        }
        return database;
    }
}
