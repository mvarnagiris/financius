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
        insertDefaultCurrencies(db);
        insertDefaultAccounts(context, db);
        insertDefaultCategories(context, db);
    }

    public static long insertDefaultCurrencies(SQLiteDatabase db)
    {
        final Set<String> currencyCodeSet = new HashSet<>();

        // Default currency
        String defaultCode = null;
        try
        {
            defaultCode = Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        }
        catch (Exception ignored)
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
        for (String code : currencyCodeSet)
        {
            try
            {
                currency = Currency.getInstance(code);
                ContentValues values = CurrenciesUtils.getValues(code, currency.getSymbol(), currency.getDefaultFractionDigits(), ",", ".", code.equals(defaultCode), Tables.Currencies.SymbolFormat.RIGHT_FAR, 1.0);
                long newId = db.insert(Tables.Currencies.TABLE_NAME, null, values);
                if (code.equalsIgnoreCase(defaultCode))
                {
                    CurrencyHelper.get().setMainCurrencyId(newId);
                    CurrencyHelper.get().setMainCurrencyCode(code);
                    mainCurrencyId = newId;
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return mainCurrencyId;
    }

    public static void insertDefaultAccounts(Context context, SQLiteDatabase db)
    {
        // Income account
        ContentValues values = AccountsUtils.getValues(0, context.getResources().getResourceName(R.string.income), "", 0, false, false);
        values.put(Tables.Accounts.ID, Tables.Accounts.IDs.INCOME_ID);
        values.put(Tables.Accounts.SERVER_ID, String.valueOf(Tables.Accounts.IDs.INCOME_ID));
        values.put(Tables.Accounts.ORIGIN, Tables.Accounts.Origin.SYSTEM);
        db.insert(Tables.Accounts.TABLE_NAME, null, values);

        // Expense account
        values = AccountsUtils.getValues(0, context.getResources().getResourceName(R.string.expense), "", 0, false, false);
        values.put(Tables.Accounts.ID, Tables.Accounts.IDs.EXPENSE_ID);
        values.put(Tables.Accounts.SERVER_ID, String.valueOf(Tables.Accounts.IDs.EXPENSE_ID));
        values.put(Tables.Accounts.ORIGIN, Tables.Accounts.Origin.SYSTEM);
        db.insert(Tables.Accounts.TABLE_NAME, null, values);
    }

    public static void insertDefaultCategories(Context context, SQLiteDatabase db)
    {
        // Income category
        ContentValues values = CategoriesUtils.getValues(0, context.getString(R.string.income), 0, Tables.Categories.Type.INCOME, context.getResources().getColor(R.color.text_green));
        values.put(Tables.Categories.ID, Tables.Categories.IDs.INCOME_ID);
        values.put(Tables.Categories.SERVER_ID, String.valueOf(Tables.Categories.IDs.INCOME_ID));
        values.put(Tables.Categories.ORDER, 0);
        values.put(Tables.Categories.PARENT_ORDER, 0);
        values.put(Tables.Categories.ORIGIN, Tables.Categories.Origin.SYSTEM);
        db.insert(Tables.Categories.TABLE_NAME, null, values);

        // Expense category
        values = CategoriesUtils.getValues(0, context.getString(R.string.expense), 0, Tables.Categories.Type.EXPENSE, context.getResources().getColor(R.color.f_maroon));
        values.put(Tables.Categories.ID, Tables.Categories.IDs.EXPENSE_ID);
        values.put(Tables.Categories.SERVER_ID, String.valueOf(Tables.Categories.IDs.EXPENSE_ID));
        values.put(Tables.Categories.ORDER, 0);
        values.put(Tables.Categories.PARENT_ORDER, 0);
        values.put(Tables.Categories.ORIGIN, Tables.Categories.Origin.SYSTEM);
        db.insert(Tables.Categories.TABLE_NAME, null, values);

        // Transfer category
        values = CategoriesUtils.getValues(0, context.getString(R.string.transfer), 0, Tables.Categories.Type.TRANSFER, context.getResources().getColor(R.color.text_yellow));
        values.put(Tables.Categories.ID, Tables.Categories.IDs.TRANSFER_ID);
        values.put(Tables.Categories.SERVER_ID, String.valueOf(Tables.Categories.IDs.TRANSFER_ID));
        values.put(Tables.Categories.ORDER, 0);
        values.put(Tables.Categories.PARENT_ORDER, 0);
        values.put(Tables.Categories.ORIGIN, Tables.Categories.Origin.SYSTEM);
        db.insert(Tables.Categories.TABLE_NAME, null, values);

        // Insert default categories
        TypedArray ta = context.getResources().obtainTypedArray(R.array.category_colors);
        //noinspection ConstantConditions
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
        JSONObject jObject;
        long newId;
        String titleRes;
        for (int i = 0; i < jArray.length(); i++)
        {
            jObject = jArray.getJSONObject(i);
            titleRes = jObject.getString("title_res");

            ContentValues values = CategoriesUtils.getValues(parentId, res.getString(res.getIdentifier(titleRes, "string", context.getPackageName())), level, type, level == 1 ? colors[(i + colorStartIndex) % colors.length] : colors[colorStartIndex]);
            values.put(Tables.Categories.SERVER_ID, titleRes);
            values.put(Tables.Categories.ORDER, i);
            values.put(Tables.Categories.PARENT_ORDER, parentOrder);
            newId = db.insert(Tables.Categories.TABLE_NAME, null, values);

            if (jObject.has("categories"))
                insertCategories(context, db, res, jObject.getJSONArray("categories"), newId, level, i, type, colors, (i + colorStartIndex) % colors.length);
        }
    }
}