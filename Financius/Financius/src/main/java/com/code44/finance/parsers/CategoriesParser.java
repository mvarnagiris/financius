package com.code44.finance.parsers;

import android.content.ContentValues;
import android.content.Context;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoriesParser extends Parser
{
    public static final String KEY_CATEGORIES = "KEY_CATEGORIES";
    public static final String KEY_SUBCATEGORIES = "KEY_SUBCATEGORIES";
    private static final String TEMP_PARENT_SERVER_ID = "TEMP_PARENT_SERVER_ID";

    public void parseCategories(List<ContentValues> categoriesList, List<ContentValues> subcategoriesList, JSONArray json) throws Exception
    {
        ContentValues values;
        JSONObject jObject;
        JSONArray jArray;
        String parentServerId;

        // Categories
        for (int i = 0; i < json.length(); i++)
        {
            // Category
            jObject = json.getJSONObject(i);
            values = new ContentValues();
            parseCategory(values, jObject, null, 1);
            categoriesList.add(values);

            if (jObject.has(JTags.Category.LIST))
            {
                // Sub-categories
                parentServerId = values.getAsString(Tables.Categories.SERVER_ID);
                jArray = jObject.getJSONArray(JTags.Category.LIST);
                for (int e = 0; e < jArray.length(); e++)
                {
                    // Sub-category
                    jObject = jArray.getJSONObject(e);
                    values = new ContentValues();
                    parseCategory(values, jObject, parentServerId, 2);
                    subcategoriesList.add(values);
                }
            }
        }
    }

    public void parseCategory(ContentValues values, JSONObject json, String parentServerId, int level) throws Exception
    {
        final int type = json.getInt(JTags.Category.TYPE);
        if (level == 1)
        {
            // Main category
            values.put(Tables.Categories.PARENT_ID, type == Tables.Categories.Type.EXPENSE ? Tables.Categories.IDs.EXPENSE_ID : Tables.Categories.IDs.INCOME_ID);
        }
        else
        {
            // Sub category
            values.put(TEMP_PARENT_SERVER_ID, parentServerId);
        }

        values.put(Tables.Categories.SERVER_ID, json.getString(JTags.Category.SERVER_ID));
        values.put(Tables.Categories.TITLE, json.getString(JTags.Category.TITLE));
        values.put(Tables.Categories.LEVEL, level);
        values.put(Tables.Categories.TYPE, type);
        values.put(Tables.Categories.ORIGIN, json.getInt(JTags.Category.ORIGIN));
        values.put(Tables.Categories.COLOR, json.getInt(JTags.Category.COLOR));
        values.put(Tables.Categories.TIMESTAMP, json.getLong(JTags.Category.TIMESTAMP));
        values.put(Tables.Categories.DELETE_STATE, json.getInt(JTags.Category.DELETE_STATE));
    }

    @Override
    public void store(Context context, ParsedValues parsedValues)
    {
//        // Categories
//        ContentValues[] valuesArray = parsedValues.getArray(KEY_CATEGORIES);
//        if (valuesArray != null && valuesArray.length > 0)
//            context.getContentResolver().bulkInsert(CategoriesProvider.uriBulkCategories(context), valuesArray);
//
//        // Sub-categories
//        valuesArray = parsedValues.getArray(KEY_SUBCATEGORIES);
//        if (valuesArray != null && valuesArray.length > 0)
//        {
//            replaceStringWithLong(
//                    valuesArray,
//                    getStringIDsMap(context, CategoriesProvider.uriCategories(context), Tables.Categories.LEVEL + "=?", new String[]{"1"},
//                            Tables.Categories.SERVER_ID, Tables.Categories.T_ID), TEMP_PARENT_SERVER_ID, Tables.Categories.PARENT_ID);
//            context.getContentResolver().bulkInsert(CategoriesProvider.uriBulkCategories(context), valuesArray);
//        }

        // Notify
        context.getContentResolver().notifyChange(CategoriesProvider.uriCategories(), null);
    }

    @Override
    protected void parse(Context context, ParsedValues parsedValues, Object info) throws Exception
    {
        // Prepare
        final List<ContentValues> categoriesList = new ArrayList<ContentValues>();
        final List<ContentValues> subcategoriesList = new ArrayList<ContentValues>();

        // Parse
        parseCategories(categoriesList, subcategoriesList, (JSONArray) info);
        parsedValues.putList(KEY_CATEGORIES, categoriesList);
        parsedValues.putList(KEY_SUBCATEGORIES, subcategoriesList);
    }
}