package com.code44.finance.db;

import android.provider.BaseColumns;

/**
 * Information about database tables.
 *
 * @author Mantas Varnagiris
 */
public class Tables
{
    public static final String SERVER_ID_SUFFIX = "server_id";
    public static final String SYNC_STATE_SUFFIX = "sync_state";
    public static final String DELETE_STATE_SUFFIX = "delete_state";
    public static final String TIMESTAMP_SUFFIX = "timestamp";

    public static class SyncState
    {
        public static final int LOCAL_CHANGES = 0;
        public static final int IN_PROGRESS = 1;
        public static final int SYNCED = 2;
    }

    public static class DeleteState
    {
        public static final int NONE = 0;
        public static final int DELETED = 1;
    }

    public static class Currencies
    {
        public static final String TABLE_NAME = "currencies";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SERVER_ID_SUFFIX;
        public static final String CODE = TABLE_NAME + "_" + "code";
        public static final String SYMBOL = TABLE_NAME + "_" + "symbol";
        public static final String DECIMALS = TABLE_NAME + "_" + "decimals";
        public static final String DECIMAL_SEPARATOR = TABLE_NAME + "_" + "decimal_separator";
        public static final String GROUP_SEPARATOR = TABLE_NAME + "_" + "group_separator";
        public static final String SYMBOL_FORMAT = TABLE_NAME + "_" + "symbol_format";
        public static final String IS_DEFAULT = TABLE_NAME + "_" + "is_default";
        public static final String EXCHANGE_RATE = TABLE_NAME + "_" + "exchange_rate";
        public static final String TIMESTAMP = TABLE_NAME + "_" + TIMESTAMP_SUFFIX;
        public static final String SYNC_STATE = TABLE_NAME + "_" + SYNC_STATE_SUFFIX;
        public static final String DELETE_STATE = TABLE_NAME + "_" + DELETE_STATE_SUFFIX;
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + SERVER_ID + " text, " + CODE + " text, " + SYMBOL + " text, " + DECIMALS + " integer, "
                + DECIMAL_SEPARATOR + " text, " + GROUP_SEPARATOR + " text, " + SYMBOL_FORMAT + " text, " + IS_DEFAULT + " boolean, "
                + EXCHANGE_RATE + " real, " + TIMESTAMP + " datetime default 0, "
                + SYNC_STATE + " integer default " + SyncState.LOCAL_CHANGES + ", " + DELETE_STATE + " integer default " + DeleteState.NONE + ");";
        // -------------------------------------------------------------------------------------------------------------

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
        public static final String SERVER_ID = TABLE_NAME + "_" + SERVER_ID_SUFFIX;
        public static final String CURRENCY_ID = TABLE_NAME + "_" + "currency_id";
        public static final String TYPE_RES_NAME = TABLE_NAME + "_" + "type_res_name";
        public static final String TITLE = TABLE_NAME + "_" + "title";
        public static final String NOTE = TABLE_NAME + "_" + "note";
        public static final String BALANCE = TABLE_NAME + "_" + "balance";
        public static final String OVERDRAFT = TABLE_NAME + "_" + "overdraft";
        public static final String SHOW_IN_TOTALS = TABLE_NAME + "_" + "show_in_totals";
        public static final String SHOW_IN_SELECTION = TABLE_NAME + "_" + "show_in_selection";
        public static final String ORIGIN = TABLE_NAME + "_" + "origin";
        public static final String TIMESTAMP = TABLE_NAME + "_" + TIMESTAMP_SUFFIX;
        public static final String SYNC_STATE = TABLE_NAME + "_" + SYNC_STATE_SUFFIX;
        public static final String DELETE_STATE = TABLE_NAME + "_" + DELETE_STATE_SUFFIX;
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " + SERVER_ID
                + " text, " + CURRENCY_ID + " integer default 0, " + TYPE_RES_NAME + " text, " + TITLE + " text, " + NOTE + " text, "
                + BALANCE + " real default 0, " + OVERDRAFT + " real default 0, " + SHOW_IN_TOTALS + " boolean default 1, "
                + SHOW_IN_SELECTION + " boolean default 1, " + ORIGIN + " integer, " + TIMESTAMP + " datetime default 0, "
                + SYNC_STATE + " integer default " + SyncState.LOCAL_CHANGES + ", " + DELETE_STATE + " integer default " + DeleteState.NONE + ");";
        // -------------------------------------------------------------------------------------------------------------

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
        public static final String SERVER_ID = TABLE_NAME + "_" + SERVER_ID_SUFFIX;
        public static final String PARENT_ID = TABLE_NAME + "_" + "parent_id";
        public static final String TITLE = TABLE_NAME + "_" + "title";
        public static final String COLOR = TABLE_NAME + "_" + "color";
        public static final String LEVEL = TABLE_NAME + "_" + "level";
        public static final String TYPE = TABLE_NAME + "_" + "type";
        public static final String ORIGIN = TABLE_NAME + "_" + "origin";
        public static final String ORDER = TABLE_NAME + "_" + "order";
        public static final String PARENT_ORDER = TABLE_NAME + "_" + "parent_order";
        public static final String TIMESTAMP = TABLE_NAME + "_" + TIMESTAMP_SUFFIX;
        public static final String SYNC_STATE = TABLE_NAME + "_" + SYNC_STATE_SUFFIX;
        public static final String DELETE_STATE = TABLE_NAME + "_" + DELETE_STATE_SUFFIX;
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " + SERVER_ID
                + " text, " + PARENT_ID + " integer, " + TITLE + " text, " + COLOR + " integer default 0, " + LEVEL + " integer default 0, "
                + TYPE + " integer, " + ORIGIN + " integer, " + ORDER + " integer," + PARENT_ORDER + " integer, "
                + TIMESTAMP + " datetime default 0, " + SYNC_STATE + " integer default " + SyncState.LOCAL_CHANGES + ", "
                + DELETE_STATE + " integer default " + DeleteState.NONE + ");";

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
        public static final String SERVER_ID = TABLE_NAME + "_" + SERVER_ID_SUFFIX;
        public static final String ACCOUNT_FROM_ID = TABLE_NAME + "_" + "account_from_id";
        public static final String ACCOUNT_TO_ID = TABLE_NAME + "_" + "account_to_id";
        public static final String CATEGORY_ID = TABLE_NAME + "_" + "category_id";
        public static final String DATE = TABLE_NAME + "_" + "date";
        public static final String AMOUNT = TABLE_NAME + "_" + "amount";
        public static final String EXCHANGE_RATE = TABLE_NAME + "_" + "exchange_rate";
        public static final String NOTE = TABLE_NAME + "_" + "note";
        public static final String STATE = TABLE_NAME + "_" + "state";
        public static final String SHOW_IN_TOTALS = TABLE_NAME + "_" + "show_in_totals";
        public static final String TIMESTAMP = TABLE_NAME + "_" + TIMESTAMP_SUFFIX;
        public static final String SYNC_STATE = TABLE_NAME + "_" + SYNC_STATE_SUFFIX;
        public static final String DELETE_STATE = TABLE_NAME + "_" + DELETE_STATE_SUFFIX;
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;
        // -------------------------------------------------------------------------------------------------------------
        public static final String C_SUM = TABLE_NAME + "_" + "c_sum";
        public static final String S_SUM = "sum(" + AMOUNT + ") as " + C_SUM;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " + SERVER_ID
                + " text, " + ACCOUNT_FROM_ID + " integer, " + ACCOUNT_TO_ID + " integer, " + CATEGORY_ID
                + " integer, " + DATE + " datetime, " + AMOUNT + " real, " + EXCHANGE_RATE + " real default 1, " + NOTE + " text, "
                + STATE + " integer default " + State.CONFIRMED + ", " + SHOW_IN_TOTALS + " boolean default 1, " + TIMESTAMP
                + " datetime, " + SYNC_STATE + " integer default " + SyncState.LOCAL_CHANGES + ", " + DELETE_STATE
                + " integer default " + DeleteState.NONE + ");";

        public static class State
        {
            public static final int CONFIRMED = 0;
            public static final int PENDING = 1;
        }
    }

    public static class Budgets
    {
        public static final String TABLE_NAME = "budgets";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SERVER_ID_SUFFIX;
        public static final String TITLE = TABLE_NAME + "_" + "title";
        public static final String NOTE = TABLE_NAME + "_" + "note";
        public static final String PERIOD = TABLE_NAME + "_" + "period";
        public static final String AMOUNT = TABLE_NAME + "_" + "amount";
        public static final String INCLUDE_IN_TOTAL_BUDGET = TABLE_NAME + "_" + "include_in_total_budget";
        public static final String SHOW_IN_OVERVIEW = TABLE_NAME + "_" + "show_in_overview";
        public static final String TIMESTAMP = TABLE_NAME + "_" + TIMESTAMP_SUFFIX;
        public static final String SYNC_STATE = TABLE_NAME + "_" + SYNC_STATE_SUFFIX;
        public static final String DELETE_STATE = TABLE_NAME + "_" + DELETE_STATE_SUFFIX;
        // -------------------------------------------------------------------------------------------------------------
        public static final String T_ID = TABLE_NAME + "." + ID;
        // -------------------------------------------------------------------------------------------------------------
        public static final String SUM = TABLE_NAME + "_" + "sum";
        public static final String CATEGORIES = TABLE_NAME + "_" + "categories";
        public static final String S_SUM = "sum(" + Transactions.AMOUNT + "*" + Currencies.EXCHANGE_RATE + ") as " + SUM;
        public static final String S_CATEGORIES = "group_concat(" + BudgetCategories.CATEGORY_ID + ") as " + CATEGORIES;
        // -------------------------------------------------------------------------------------------------------------
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + SERVER_ID + " text, " + TITLE + " text, " + NOTE + " text, " + AMOUNT + " real, " + PERIOD + " integer, "
                + INCLUDE_IN_TOTAL_BUDGET + " boolean default 1, " + SHOW_IN_OVERVIEW + " boolean default 0, " + TIMESTAMP + " datetime default 0, " + SYNC_STATE
                + " integer default " + SyncState.LOCAL_CHANGES + ", " + DELETE_STATE + " integer default " + DeleteState.NONE + ");";
    }

    public static class BudgetCategories
    {
        public static final String TABLE_NAME = "budget_categories";
        // -------------------------------------------------------------------------------------------------------------
        public static final String BUDGET_ID = TABLE_NAME + "_" + "budget_id";
        public static final String CATEGORY_ID = TABLE_NAME + "_" + "category_id";
        // -------------------------------------------------------------------------------------------------------------
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + BUDGET_ID + " integer, "
                + CATEGORY_ID + " integer, primary key (" + BUDGET_ID + ", " + CATEGORY_ID + "));";
    }

    public static class RecurringTransactions
    {
        public static final String TABLE_NAME = "recurring_transactions";
        public static final String ID = BaseColumns._ID;
        public static final String SERVER_ID = TABLE_NAME + "_" + SERVER_ID_SUFFIX;
        public static final String ACCOUNT_FROM_ID = TABLE_NAME + "_" + "account_from_id";
        public static final String ACCOUNT_TO_ID = TABLE_NAME + "_" + "account_to_id";
        public static final String CATEGORY_ID = TABLE_NAME + "_" + "category_id";
        public static final String AMOUNT = TABLE_NAME + "_" + "amount";
        public static final String NOTE = TABLE_NAME + "_" + "note";
        public static final String START_DATE = TABLE_NAME + "_" + "start_date";
        public static final String END_DATE = TABLE_NAME + "_" + "end_date";
        public static final String END_AFTER = TABLE_NAME + "_" + "end_after";
        public static final String CREATED_COUNT = TABLE_NAME + "_" + "created_count";
        public static final String WEEKDAYS = TABLE_NAME + "_" + "weekdays";
        public static final String TIMESTAMP = TABLE_NAME + "_" + TIMESTAMP_SUFFIX;
        public static final String SYNC_STATE = TABLE_NAME + "_" + SYNC_STATE_SUFFIX;
        public static final String DELETE_STATE = TABLE_NAME + "_" + DELETE_STATE_SUFFIX;
        public static final String T_ID = TABLE_NAME + "." + ID;
        public static final String CREATE_SCRIPT = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, " + SERVER_ID
                + " text, " + ACCOUNT_FROM_ID + " integer, " + ACCOUNT_TO_ID + " integer, " + CATEGORY_ID + " integer, " + AMOUNT + " real, " + NOTE + " text, "
                + START_DATE + " datetime, " + END_DATE + " datetime, " + END_AFTER + " integer, " + CREATED_COUNT + " integer, " + WEEKDAYS + " integer, "
                + TIMESTAMP + " datetime default 0, " + SYNC_STATE + " integer default " + SyncState.LOCAL_CHANGES + ", "
                + DELETE_STATE + " integer default " + DeleteState.NONE + ");";

        public static class Weekdays
        {
            public static final int MONDAY = 0x0000001;
            public static final int TUESDAY = 0x0000010;
            public static final int WEDNESDAY = 0x0000100;
            public static final int THURSDAY = 0x0001000;
            public static final int FRIDAY = 0x0010000;
            public static final int SATURDAY = 0x0100000;
            public static final int SUNDAY = 0x1000000;
        }
    }
}