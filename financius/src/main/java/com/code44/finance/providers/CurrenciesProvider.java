package com.code44.finance.providers;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.code44.finance.db.model.Currency;

public class CurrenciesProvider extends BaseProvider {
    public static Uri uriCurrencies() {
        return Uri.parse(CONTENT_URI_BASE + getAuthority(CurrenciesProvider.class) + "/" + Currency.class.getSimpleName());
    }

    public static Uri uriCurrency(long currencyId) {
        return ContentUris.withAppendedId(uriCurrencies(), currencyId);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
