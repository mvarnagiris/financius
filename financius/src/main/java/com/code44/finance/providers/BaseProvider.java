package com.code44.finance.providers;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.BuildConfig;
import com.code44.finance.db.DBHelper;

public abstract class BaseProvider extends ContentProvider {
    protected static final String CONTENT_URI_BASE = "content://";

    protected static final String TYPE_LIST_BASE = "vnd.android.cursor.dir/vnd.code44.";
    protected static final String TYPE_ITEM_BASE = "vnd.android.cursor.item/vnd.code44.";

    protected UriMatcher uriMatcher;
    protected SQLiteDatabase database;

    protected static String getAuthority(Class<? extends BaseProvider> cls) {
        return BuildConfig.PACKAGE_NAME + ".providers." + cls.getSimpleName();
    }

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        database = DBHelper.get(getContext()).getWritableDatabase();
        return (database != null);
    }

    protected String getAuthority() {
        return getAuthority(getClass());
    }
}
