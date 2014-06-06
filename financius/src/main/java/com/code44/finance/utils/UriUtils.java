package com.code44.finance.utils;

import android.content.Context;
import android.net.Uri;

public final class UriUtils {

    private UriUtils() {
    }

    public static Uri addParam(Uri uri, QueryParameterKey key, String value) {
        return uri.buildUpon().appendQueryParameter(key.getKeyName(), value).build();
    }

    public static void notifyChangeIfNecessary(Context context, Uri uri) {
        boolean notifyUriChanged = uri.getBooleanQueryParameter(QueryParameterKey.NOTIFY_URI_CHANGED.getKeyName(), true);
        if (notifyUriChanged) {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    public static enum QueryParameterKey {
        /**
         * Possible values: {@code "true"} and {@code "false"}.
         */
        NOTIFY_URI_CHANGED("notifyUriChanged");

        private final String keyName;

        private QueryParameterKey(String keyName) {
            this.keyName = keyName;
        }

        public String getKeyName() {
            return keyName;
        }
    }

}
