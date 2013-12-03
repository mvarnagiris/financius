package com.code44.finance.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.code44.finance.R;
import com.code44.finance.utils.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class DBDefaults
{
    public static void insertDefaults(Context context, SQLiteDatabase db)
    {
        insertDefaultCurrencies(context, db);
        insertDefaultAccounts(context, db);
        insertDefaultCategories(context, db);
    }

    public static long insertDefaultCurrencies(Context context, SQLiteDatabase db)
    {
        final Set<String> currencyCodeSet = new HashSet<String>();

        // Default currency
        String defaultCode = null;
        try
        {
            defaultCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        }
        catch (Exception e)
        {
        }

        if (TextUtils.isEmpty(defaultCode))
            defaultCode = "USD";
        currencyCodeSet.add(defaultCode);

        // Popular currencies
        currencyCodeSet.add("USD");
        currencyCodeSet.add("EUR");
        currencyCodeSet.add("GBP");
        currencyCodeSet.add("CNY");
        currencyCodeSet.add("INR");
        currencyCodeSet.add("RUB");
        currencyCodeSet.add("JPY");

        // Create currencies
        Currency currency;
        long mainCurrencyId = 0;
        final ContentValues values = new ContentValues();
        for (String code : currencyCodeSet)
        {
            try
            {
                currency = Currency.getInstance(code);
                values.clear();
                CurrenciesUtils.prepareValues(values, code, currency.getSymbol(), currency.getDefaultFractionDigits(), ",", ".", code.equals(defaultCode), Tables.Currencies.SymbolFormat.RIGHT_FAR, 1.0, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
                long newId = db.insert(Tables.Currencies.TABLE_NAME, null, values);
                if (code.equalsIgnoreCase(defaultCode))
                {
                    CurrenciesHelper.getDefault().setMainCurrencyId(newId);
                    CurrenciesHelper.getDefault().setMainCurrencyCode(code);
                    mainCurrencyId = newId;
                }
            }
            catch (Exception e)
            {
            }
        }

        return mainCurrencyId;
    }

    public static void insertDefaultAccounts(Context context, SQLiteDatabase db)
    {
        final ContentValues values = new ContentValues();

        // Income account
        values.clear();
        values.put(Tables.Accounts.ID, Tables.Accounts.IDs.INCOME_ID);
        AccountsUtils.prepareValues(values, String.valueOf(Tables.Accounts.IDs.INCOME_ID), 0, context.getResources().getResourceName(R.string.ac_other), context.getString(R.string.income), "", 0, 0, false, false, Tables.Accounts.Origin.SYSTEM, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
        db.insert(Tables.Accounts.TABLE_NAME, null, values);

        // Expense account
        values.clear();
        values.put(Tables.Accounts.ID, Tables.Accounts.IDs.EXPENSE_ID);
        AccountsUtils.prepareValues(values, String.valueOf(Tables.Accounts.IDs.EXPENSE_ID), 0, context.getResources().getResourceName(R.string.ac_other), context.getString(R.string.expense), "", 0, 0, false, false, Tables.Accounts.Origin.SYSTEM, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
        db.insert(Tables.Accounts.TABLE_NAME, null, values);
    }

    public static void insertDefaultCategories(Context context, SQLiteDatabase db)
    {
        final ContentValues values = new ContentValues();

        // Income category
        values.clear();
        values.put(Tables.Categories.ID, Tables.Categories.IDs.INCOME_ID);
        CategoriesUtils.prepareValues(values, String.valueOf(Tables.Categories.IDs.INCOME_ID), 0, context.getString(R.string.income), 0, Tables.Categories.Type.INCOME, context.getResources().getColor(R.color.text_green), 0, 0, Tables.Categories.Origin.SYSTEM, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
        db.insert(Tables.Categories.TABLE_NAME, null, values);

        // Expense category
        values.clear();
        values.put(Tables.Categories.ID, Tables.Categories.IDs.EXPENSE_ID);
        CategoriesUtils.prepareValues(values, String.valueOf(Tables.Categories.IDs.EXPENSE_ID), 0, context.getString(R.string.expense), 0, Tables.Categories.Type.EXPENSE, context.getResources().getColor(R.color.f_maroon), 0, 0, Tables.Categories.Origin.SYSTEM, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
        db.insert(Tables.Categories.TABLE_NAME, null, values);

        // Transfer category
        values.clear();
        values.put(Tables.Categories.ID, Tables.Categories.IDs.TRANSFER_ID);
        CategoriesUtils.prepareValues(values, String.valueOf(Tables.Categories.IDs.TRANSFER_ID), 0, context.getString(R.string.transfer), 0, Tables.Categories.Type.TRANSFER, context.getResources().getColor(R.color.text_yellow), 0, 0, Tables.Categories.Origin.SYSTEM, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
        db.insert(Tables.Categories.TABLE_NAME, null, values);

        // Insert default categories
        TypedArray ta = context.getResources().obtainTypedArray(R.array.category_colors);
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
        {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        try
        {
            db.beginTransaction();

            // Ger categories JSON
            final JSONObject jCategories = new JSONObject(StringUtils.readInputStream(context.getAssets().open("categories.json")));

            // Insert categories
            insertCategories(context, db, context.getResources(), jCategories.getJSONArray("expense"), Tables.Categories.IDs.EXPENSE_ID, 0, 0, Tables.Categories.Type.EXPENSE, colors, 0);
            insertCategories(context, db, context.getResources(), jCategories.getJSONArray("income"), Tables.Categories.IDs.INCOME_ID, 0, 0, Tables.Categories.Type.INCOME, colors, jCategories.getJSONArray("expense").length());

            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }

    public static void insertCategories(Context context, SQLiteDatabase db, Resources res, JSONArray jArray, long parentId, int parentLevel, int parentOrder, int type, int[] colors, int colorStartIndex) throws Exception
    {
        final int level = parentLevel + 1;
        final ContentValues values = new ContentValues();
        JSONObject jObject;
        long newId;
        String titleRes;
        for (int i = 0; i < jArray.length(); i++)
        {
            jObject = jArray.getJSONObject(i);
            titleRes = jObject.getString("title_res");

            values.clear();
            CategoriesUtils.prepareValues(values, titleRes, parentId, res.getString(res.getIdentifier(titleRes, "string", context.getPackageName())), level, type, level == 1 ? colors[(i + colorStartIndex) % colors.length] : colors[colorStartIndex], i, parentOrder, Tables.Categories.Origin.USER, Tables.DeleteState.NONE, Tables.SyncState.LOCAL_CHANGES);
            newId = db.insert(Tables.Categories.TABLE_NAME, null, values);

            if (jObject.has("categories"))
                insertCategories(context, db, res, jObject.getJSONArray("categories"), newId, level, i, type, colors, (i + colorStartIndex) % colors.length);
        }
    }
}