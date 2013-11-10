package com.code44.finance.parsers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract parser class. Takes care of parsing and storing data.
 *
 * @author Mantas Varnagiris
 */
public abstract class Parser
{
    // Public methods
    // --------------------------------------------------------------------------------------------------------------------------

    /**
     * Parses values.
     *
     * @param info Object to parse.
     * @return Object that contains parsed values.
     * @throws org.json.JSONException
     */
    public ParsedValues parse(Context context, Object info) throws Exception
    {
        final ParsedValues parsedValues = new ParsedValues();
        parse(context, parsedValues, info);
        return parsedValues;
    }

    /**
     * Parses values and persists them.
     *
     * @param context
     * @param info
     * @return
     * @throws org.json.JSONException
     */
    public ParsedValues parseAndStore(Context context, Object info) throws Exception
    {
        ParsedValues parsedValues = parse(context, info);
        store(context, parsedValues);
        return parsedValues;
    }

    // Protected methods
    // --------------------------------------------------------------------------------------------------------------------------

    protected Map<String, Long> getStringIDsMap(Context context, Uri uri, String selection, String[] selectionArgs, String stringColumn, String idColumn)
    {
        Map<String, Long> stringIDsMap = new HashMap<String, Long>();

        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(uri, new String[]{idColumn, stringColumn}, selection, selectionArgs, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    stringIDsMap.put(c.getString(1), c.getLong(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return stringIDsMap;
    }

    protected Map<Long, Long> getLongIDsMap(Context context, Uri uri, String selection, String[] selectionArgs, String longColumn, String idColumn)
    {
        Map<Long, Long> longIDsMap = new HashMap<Long, Long>();

        Cursor c = null;
        try
        {
            c = context.getContentResolver().query(uri, new String[]{idColumn, longColumn}, selection, selectionArgs, null);
            if (c != null && c.moveToFirst())
            {
                do
                {
                    longIDsMap.put(c.getLong(1), c.getLong(0));
                }
                while (c.moveToNext());
            }
        }
        finally
        {
            if (c != null && !c.isClosed())
                c.close();
        }

        return longIDsMap;
    }

    protected void replaceStringWithLong(ContentValues[] valuesArray, Map<String, Long> replaceMap, String tempColumn, String newColumn)
    {
        if (valuesArray != null && valuesArray.length > 0)
        {
            Long newValue;
            String tempValue;
            for (ContentValues values : valuesArray)
            {
                tempValue = values.getAsString(tempColumn);
                if (tempValue != null)
                    newValue = replaceMap.get(tempValue);
                else
                    newValue = 0L;
                if (newValue == null)
                    newValue = 0L;
                values.remove(tempColumn);
                values.put(newColumn, newValue);
            }
        }
    }

    protected void replaceLongWithLong(ContentValues[] valuesArray, Map<Long, Long> replaceMap, String tempColumn, String newColumn)
    {
        if (valuesArray != null && valuesArray.length > 0)
        {
            long newValue;
            Long tempValue;
            for (ContentValues values : valuesArray)
            {
                tempValue = values.getAsLong(tempColumn);
                if (tempValue != null)
                    newValue = replaceMap.get(tempValue);
                else
                    newValue = 0;
                values.remove(tempColumn);
                values.put(newColumn, newValue);
            }
        }
    }

    // Abstract methods
    // --------------------------------------------------------------------------------------------------------------------------

    /**
     * Parses values and puts them in {@link ParsedValues} object.
     *
     * @param parsedValues Put parsed values here.
     * @param info         Object to parse.
     * @throws org.json.JSONException
     */
    protected abstract void parse(Context context, ParsedValues parsedValues, Object info) throws Exception;

    /**
     * Persists values.
     *
     * @param context      Context.
     * @param parsedValues Parsed values.
     */
    public abstract void store(Context context, ParsedValues parsedValues);

    // ParsedValues
    // --------------------------------------------------------------------------------------------------------------------------

    /**
     * Object that contains parsed {@link android.content.ContentValues} arrays and single objects.
     *
     * @author Mantas Varnagiris
     */
    public static class ParsedValues
    {
        public final Map<String, ContentValues> parsedObjectsMap;
        public final Map<String, ContentValues[]> parsedArraysMap;

        public ParsedValues()
        {
            parsedObjectsMap = new HashMap<String, ContentValues>();
            parsedArraysMap = new HashMap<String, ContentValues[]>();
        }

        // Public methods
        // --------------------------------------------------------------------------------------------------------------------------

        public void putObject(String key, ContentValues values)
        {
            parsedObjectsMap.put(key, values);
        }

        public void putArray(String key, ContentValues[] valuesArray)
        {
            parsedArraysMap.put(key, valuesArray);
        }

        /**
         * Converts list to array and calls {@link ParsedValues#putArray(String, android.content.ContentValues[])}.
         *
         * @param key
         * @param valuesList
         */
        public void putList(String key, List<ContentValues> valuesList)
        {
            ContentValues[] valuesArray = new ContentValues[valuesList.size()];
            valuesList.toArray(valuesArray);
            putArray(key, valuesArray);
        }

        public ContentValues getObject(String key)
        {
            return parsedObjectsMap.get(key);
        }

        public ContentValues[] getArray(String key)
        {
            return parsedArraysMap.get(key);
        }

        public boolean hasObject(String key)
        {
            return parsedObjectsMap.containsKey(key);
        }

        public boolean hasArray(String key)
        {
            return parsedArraysMap.containsKey(key);
        }
    }
}