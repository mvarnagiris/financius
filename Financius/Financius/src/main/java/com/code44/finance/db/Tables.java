package com.code44.finance.db;

import android.provider.BaseColumns;
import android.text.TextUtils;

public class Tables
{
    public static final String SUFFIX_SERVER_ID = "server_id";
    public static final String SUFFIX_DELETE_STATE = "delete_state";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String TYPE_TEXT = "text";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_REAL = "real";
    private static final String TYPE_DATE = "datetime";
    private static final String TYPE_BOOLEAN = "boolean";

    public static class DeleteState
    {
        public static final int NONE = 0;
        public static final int DELETED = 1;
    }

    private static String makeColumn(String name, String type, String defaultValue)
    {
        return name + " " + type + (TextUtils.isEmpty(defaultValue) ? "" : " default " + defaultValue);
    }

    private static String makeCreateScript(String table, String... columns)
    {
        final StringBuilder sb = new StringBuilder("create table ");
        sb.append(table);
        sb.append(" (");

        sb.append(BaseColumns._ID).append(" integer primary key autoincrement, ");
        sb.append(table).append("_").append(SUFFIX_SERVER_ID).append(" ").append(TYPE_TEXT).append(", ");
        sb.append(table).append("_").append(SUFFIX_DELETE_STATE).append(" ").append(TYPE_INTEGER).append(" default ").append(DeleteState.NONE);

        if (columns != null)
        {
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < columns.length; i++)
                sb.append(", ").append(columns[i]);
        }

        sb.append(");");

        return sb.toString();
    }

    public static class Currencies
    {
        public static final String TABLE_NAME = "currencies";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SUFFIX_SERVER_ID;
        public static final String DELETE_STATE = TABLE_NAME + "_" + SUFFIX_DELETE_STATE;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CODE = TABLE_NAME + "_" + "code";
        public static final String SYMBOL = TABLE_NAME + "_" + "symbol";
        public static final String DECIMALS = TABLE_NAME + "_" + "decimals";
        public static final String DECIMAL_SEPARATOR = TABLE_NAME + "_" + "decimal_separator";
        public static final String GROUP_SEPARATOR = TABLE_NAME + "_" + "group_separator";
        public static final String SYMBOL_FORMAT = TABLE_NAME + "_" + "symbol_format";
        public static final String IS_DEFAULT = TABLE_NAME + "_" + "is_default";
        public static final String EXCHANGE_RATE = TABLE_NAME + "_" + "exchange_rate";
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;

        public static String createScript()
        {
            return makeCreateScript(TABLE_NAME,
                    makeColumn(CODE, TYPE_TEXT, null),
                    makeColumn(SYMBOL, TYPE_TEXT, null),
                    makeColumn(DECIMALS, TYPE_INTEGER, null),
                    makeColumn(DECIMAL_SEPARATOR, TYPE_TEXT, null),
                    makeColumn(GROUP_SEPARATOR, TYPE_TEXT, null),
                    makeColumn(SYMBOL_FORMAT, TYPE_TEXT, null),
                    makeColumn(IS_DEFAULT, TYPE_BOOLEAN, "0"),
                    makeColumn(EXCHANGE_RATE, TYPE_REAL, "1"));
        }

        public static class SymbolFormat
        {
            public static final String LEFT_FAR = "LF";
            public static final String LEFT_CLOSE = "LC";
            public static final String RIGHT_FAR = "RF";
            public static final String RIGHT_CLOSE = "RC";
        }

        public class CurrencyFrom
        {
            public static final String TABLE_NAME = Currencies.TABLE_NAME + "_" + "currency_from";
            // ---------------------------------------------------------------------------------------------------------
            public static final String CODE = TABLE_NAME + "_" + Currencies.CODE;
            public static final String EXCHANGE_RATE = TABLE_NAME + "_" + Currencies.EXCHANGE_RATE;
            // ---------------------------------------------------------------------------------------------------------
            public static final String T_ID = TABLE_NAME + "." + Currencies.ID;
            public static final String T_CODE = TABLE_NAME + "." + Currencies.CODE;
            public static final String T_EXCHANGE_RATE = TABLE_NAME + "." + Currencies.EXCHANGE_RATE;
            // ---------------------------------------------------------------------------------------------------------
            public static final String S_CODE = T_CODE + " as " + CODE;
            public static final String S_EXCHANGE_RATE = T_EXCHANGE_RATE + " as " + EXCHANGE_RATE;
        }

        public class CurrencyTo
        {
            public static final String TABLE_NAME = Currencies.TABLE_NAME + "_" + "currency_to";
            // ---------------------------------------------------------------------------------------------------------
            public static final String CODE = TABLE_NAME + "_" + Currencies.CODE;
            public static final String EXCHANGE_RATE = TABLE_NAME + "_" + Currencies.EXCHANGE_RATE;
            // ---------------------------------------------------------------------------------------------------------
            public static final String T_ID = TABLE_NAME + "." + Currencies.ID;
            public static final String T_CODE = TABLE_NAME + "." + Currencies.CODE;
            public static final String T_EXCHANGE_RATE = TABLE_NAME + "." + Currencies.EXCHANGE_RATE;
            // ---------------------------------------------------------------------------------------------------------
            public static final String S_CODE = T_CODE + " as " + CODE;
            public static final String S_EXCHANGE_RATE = T_EXCHANGE_RATE + " as " + EXCHANGE_RATE;
        }
    }

    public static class Accounts
    {
        public static final String TABLE_NAME = "accounts";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SUFFIX_SERVER_ID;
        public static final String DELETE_STATE = TABLE_NAME + "_" + SUFFIX_DELETE_STATE;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CURRENCY_ID = TABLE_NAME + "_" + "currency_id";
        public static final String TITLE = TABLE_NAME + "_" + "title";
        public static final String NOTE = TABLE_NAME + "_" + "note";
        public static final String BALANCE = TABLE_NAME + "_" + "balance";
        public static final String SHOW_IN_TOTALS = TABLE_NAME + "_" + "show_in_totals";
        public static final String SHOW_IN_SELECTION = TABLE_NAME + "_" + "show_in_selection";
        public static final String ORIGIN = TABLE_NAME + "_" + "origin";
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;

        public static String createScript()
        {
            return makeCreateScript(TABLE_NAME,
                    makeColumn(CURRENCY_ID, TYPE_INTEGER, null),
                    makeColumn(TITLE, TYPE_TEXT, null),
                    makeColumn(NOTE, TYPE_TEXT, null),
                    makeColumn(BALANCE, TYPE_REAL, "0"),
                    makeColumn(SHOW_IN_TOTALS, TYPE_BOOLEAN, "1"),
                    makeColumn(SHOW_IN_SELECTION, TYPE_BOOLEAN, "1"),
                    makeColumn(ORIGIN, TYPE_INTEGER, null));
        }

        public static class IDs
        {
            public static final long INCOME_ID = 1;
            public static final long EXPENSE_ID = 2;
        }

        public static class Origin
        {
            public static final int SYSTEM = 0;
            public static final int USER = 1;
        }

        public class AccountFrom
        {
            public static final String TABLE_NAME = Accounts.TABLE_NAME + "_" + "account_from";
            // ---------------------------------------------------------------------------------------------------------
            public static final String SERVER_ID = TABLE_NAME + "_" + Accounts.SERVER_ID;
            public static final String CURRENCY_ID = TABLE_NAME + "_" + Accounts.CURRENCY_ID;
            public static final String TITLE = TABLE_NAME + "_" + Accounts.TITLE;
            public static final String ORIGIN = TABLE_NAME + "_" + Accounts.ORIGIN;
            // ---------------------------------------------------------------------------------------------------------
            public static final String T_ID = TABLE_NAME + "." + Accounts.ID;
            public static final String T_SERVER_ID = TABLE_NAME + "." + Accounts.SERVER_ID;
            public static final String T_CURRENCY_ID = TABLE_NAME + "." + Accounts.CURRENCY_ID;
            public static final String T_TITLE = TABLE_NAME + "." + Accounts.TITLE;
            public static final String T_ORIGIN = TABLE_NAME + "." + Accounts.ORIGIN;
            // ---------------------------------------------------------------------------------------------------------
            public static final String S_SERVER_ID = T_SERVER_ID + " as " + SERVER_ID;
            public static final String S_CURRENCY_ID = T_CURRENCY_ID + " as " + CURRENCY_ID;
            public static final String S_TITLE = T_TITLE + " as " + TITLE;
            public static final String S_ORIGIN = T_ORIGIN + " as " + ORIGIN;
        }

        public class AccountTo
        {
            public static final String TABLE_NAME = Accounts.TABLE_NAME + "_" + "account_to";
            // ---------------------------------------------------------------------------------------------------------
            public static final String SERVER_ID = TABLE_NAME + "_" + Accounts.SERVER_ID;
            public static final String CURRENCY_ID = TABLE_NAME + "_" + Accounts.CURRENCY_ID;
            public static final String TITLE = TABLE_NAME + "_" + Accounts.TITLE;
            public static final String ORIGIN = TABLE_NAME + "_" + Accounts.ORIGIN;
            // ---------------------------------------------------------------------------------------------------------
            public static final String T_ID = TABLE_NAME + "." + Accounts.ID;
            public static final String T_SERVER_ID = TABLE_NAME + "." + Accounts.SERVER_ID;
            public static final String T_CURRENCY_ID = TABLE_NAME + "." + Accounts.CURRENCY_ID;
            public static final String T_TITLE = TABLE_NAME + "." + Accounts.TITLE;
            public static final String T_ORIGIN = TABLE_NAME + "." + Accounts.ORIGIN;
            // ---------------------------------------------------------------------------------------------------------
            public static final String S_SERVER_ID = T_SERVER_ID + " as " + SERVER_ID;
            public static final String S_CURRENCY_ID = T_CURRENCY_ID + " as " + CURRENCY_ID;
            public static final String S_TITLE = T_TITLE + " as " + TITLE;
            public static final String S_ORIGIN = T_ORIGIN + " as " + ORIGIN;
        }
    }

    public static class Categories
    {
        public static final String TABLE_NAME = "categories";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SUFFIX_SERVER_ID;
        public static final String DELETE_STATE = TABLE_NAME + "_" + SUFFIX_DELETE_STATE;
        // -------------------------------------------------------------------------------------------------------------
        public static final String PARENT_ID = TABLE_NAME + "_" + "parent_id";
        public static final String TITLE = TABLE_NAME + "_" + "title";
        public static final String COLOR = TABLE_NAME + "_" + "color";
        public static final String LEVEL = TABLE_NAME + "_" + "level";
        public static final String TYPE = TABLE_NAME + "_" + "type";
        public static final String ORIGIN = TABLE_NAME + "_" + "origin";
        public static final String ORDER = TABLE_NAME + "_" + "order";
        public static final String PARENT_ORDER = TABLE_NAME + "_" + "parent_order";
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;

        public static String createScript()
        {
            return makeCreateScript(TABLE_NAME,
                    makeColumn(PARENT_ID, TYPE_INTEGER, null),
                    makeColumn(TITLE, TYPE_TEXT, null),
                    makeColumn(COLOR, TYPE_INTEGER, "0"),
                    makeColumn(LEVEL, TYPE_INTEGER, "0"),
                    makeColumn(TYPE, TYPE_INTEGER, null),
                    makeColumn(ORIGIN, TYPE_INTEGER, null),
                    makeColumn(ORDER, TYPE_INTEGER, null),
                    makeColumn(PARENT_ORDER, TYPE_INTEGER, null));
        }

        // Constants
        // --------------------------------------------------------------------------------------------------------------------------------

        public static class IDs
        {
            public static final long INCOME_ID = 1;
            public static final long EXPENSE_ID = 2;
            public static final long TRANSFER_ID = 3;
        }

        public static class Type
        {
            public static final int INCOME = 0;
            public static final int EXPENSE = 1;
            public static final int TRANSFER = 2;
        }

        public static class Origin
        {
            public static final int SYSTEM = 0;
            public static final int USER = 1;
        }

        // Calculated tables
        // --------------------------------------------------------------------------------------------------------------------------------

        public class CategoriesChild
        {
            public static final String TABLE_NAME = Categories.TABLE_NAME + "_" + "categories_child";
            // ---------------------------------------------------------------------------------------------------------
            public static final String SERVER_ID = TABLE_NAME + "_" + Categories.SERVER_ID;
            public static final String PARENT_ID = TABLE_NAME + "_" + Categories.PARENT_ID;
            public static final String TITLE = TABLE_NAME + "_" + Categories.TITLE;
            public static final String COLOR = TABLE_NAME + "_" + Categories.COLOR;
            public static final String TYPE = TABLE_NAME + "_" + Categories.TYPE;
            public static final String LEVEL = TABLE_NAME + "_" + Categories.LEVEL;
            // ---------------------------------------------------------------------------------------------------------
            public static final String T_ID = TABLE_NAME + "." + Categories.ID;
            public static final String T_SERVER_ID = TABLE_NAME + "." + Categories.SERVER_ID;
            public static final String T_PARENT_ID = TABLE_NAME + "." + Categories.PARENT_ID;
            public static final String T_TITLE = TABLE_NAME + "." + Categories.TITLE;
            public static final String T_COLOR = TABLE_NAME + "." + Categories.COLOR;
            public static final String T_TYPE = TABLE_NAME + "." + Categories.TYPE;
            public static final String T_LEVEL = TABLE_NAME + "." + Categories.LEVEL;
            // ---------------------------------------------------------------------------------------------------------
            public static final String S_SERVER_ID = T_SERVER_ID + " as " + SERVER_ID;
            public static final String S_PARENT_ID = T_PARENT_ID + " as " + PARENT_ID;
            public static final String S_TITLE = T_TITLE + " as " + TITLE;
            public static final String S_COLOR = T_COLOR + " as " + COLOR;
            public static final String S_TYPE = T_TYPE + " as " + TYPE;
            public static final String S_LEVEL = T_LEVEL + " as " + LEVEL;
        }

        public class CategoriesParent
        {
            public static final String TABLE_NAME = Categories.TABLE_NAME + "_" + "categories_parent";
            // ---------------------------------------------------------------------------------------------------------
            public static final String TITLE = TABLE_NAME + "_" + Categories.TITLE;
            // ---------------------------------------------------------------------------------------------------------
            public static final String T_ID = TABLE_NAME + "." + Categories.ID;
            public static final String T_TITLE = TABLE_NAME + "." + Categories.TITLE;
            // ---------------------------------------------------------------------------------------------------------
            public static final String S_TITLE = T_TITLE + " as " + TITLE;
        }

        public class CategoriesBudget
        {
            public static final String TABLE_NAME = Categories.TABLE_NAME + "_" + "categories_budget";
            public static final String TITLE = TABLE_NAME + "_" + Categories.TITLE;
            public static final String T_ID = TABLE_NAME + "." + Categories.ID;
            public static final String S_TITLE = TABLE_NAME + "." + Categories.TITLE + " as " + TITLE;
        }

        public class CategoriesBudgetTransactions
        {
            public static final String TABLE_NAME = Categories.TABLE_NAME + "_" + "categories_budget_transactions";
            public static final String T_ID = TABLE_NAME + "." + Categories.ID;
            public static final String T_PARENT_ID = TABLE_NAME + "." + Categories.PARENT_ID;
        }
    }

    public static class Transactions
    {
        public static final String TABLE_NAME = "transactions";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SUFFIX_SERVER_ID;
        public static final String DELETE_STATE = TABLE_NAME + "_" + SUFFIX_DELETE_STATE;
        // -------------------------------------------------------------------------------------------------------------
        public static final String ACCOUNT_FROM_ID = TABLE_NAME + "_" + "account_from_id";
        public static final String ACCOUNT_TO_ID = TABLE_NAME + "_" + "account_to_id";
        public static final String CATEGORY_ID = TABLE_NAME + "_" + "category_id";
        public static final String DATE = TABLE_NAME + "_" + "date";
        public static final String AMOUNT = TABLE_NAME + "_" + "amount";
        public static final String EXCHANGE_RATE = TABLE_NAME + "_" + "exchange_rate";
        public static final String NOTE = TABLE_NAME + "_" + "note";
        public static final String STATE = TABLE_NAME + "_" + "state";
        public static final String SHOW_IN_TOTALS = TABLE_NAME + "_" + "show_in_totals";
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;
        // -------------------------------------------------------------------------------------------------------------
        public static final String C_SUM = TABLE_NAME + "_" + "c_sum";
        public static final String S_SUM = "sum(" + AMOUNT + ") as " + C_SUM;


        public static final String createScript()
        {
            return makeCreateScript(TABLE_NAME,
                    makeColumn(ACCOUNT_FROM_ID, TYPE_INTEGER, null),
                    makeColumn(ACCOUNT_TO_ID, TYPE_INTEGER, null),
                    makeColumn(CATEGORY_ID, TYPE_INTEGER, null),
                    makeColumn(DATE, TYPE_DATE, "0"),
                    makeColumn(AMOUNT, TYPE_REAL, "0"),
                    makeColumn(EXCHANGE_RATE, TYPE_REAL, "1.0"),
                    makeColumn(NOTE, TYPE_TEXT, null),
                    makeColumn(STATE, TYPE_INTEGER, String.valueOf(State.CONFIRMED)),
                    makeColumn(SHOW_IN_TOTALS, TYPE_BOOLEAN, "1"));
        }

        public static class State
        {
            public static final int CONFIRMED = 0;
            public static final int PENDING = 1;
        }
    }
}