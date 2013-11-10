package com.code44.finance.parsers;

public class JTags
{
    public static class Export
    {
        public static final String DATE = "date";
        public static final String DATE_TS = "date_ts";
        public static final String VERSION = "version";
    }

    public static class Currency
    {
        public static final String LIST = "currencies";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = "id";
        public static final String SERVER_ID = "server_id";
        public static final String CODE = "code";
        public static final String SYMBOL = "symbol";
        public static final String DECIMALS = "decimals";
        public static final String DECIMAL_SEPARATOR = "decimal_separator";
        public static final String GROUP_SEPARATOR = "group_separator";
        public static final String SYMBOL_FORMAT = "symbol_format";
        public static final String IS_DEFAULT = "is_default";
        public static final String EXCHANGE_RATE = "exchange_rate";
        public static final String TIMESTAMP = "timestamp";
        public static final String SYNC_STATE = "sync_state";
        public static final String DELETE_STATE = "delete_state";
    }

    public static class Account
    {
        public static final String LIST = "accounts";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = "id";
        public static final String SERVER_ID = "server_id";
        public static final String CURRENCY_ID = "currency_id";
        public static final String TYPE_RES_NAME = "type_res_name";
        public static final String TITLE = "title";
        public static final String NOTE = "note";
        public static final String BALANCE = "balance";
        public static final String OVERDRAFT = "overdraft";
        public static final String SHOW_IN_TOTALS = "show_in_totals";
        public static final String SHOW_IN_SELECTION = "show_in_selection";
        public static final String ORIGIN = "origin";
        public static final String TIMESTAMP = "timestamp";
        public static final String SYNC_STATE = "sync_state";
        public static final String DELETE_STATE = "delete_state";
    }

    public static class Category
    {
        public static final String LIST = "categories";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = "id";
        public static final String SERVER_ID = "server_id";
        public static final String PARENT_ID = "parent_id";
        public static final String TITLE = "title";
        public static final String LEVEL = "level";
        public static final String TYPE = "type";
        public static final String COLOR = "color";
        public static final String ORIGIN = "origin";
        public static final String ORDER = "order";
        public static final String PARENT_ORDER = "parent_order";
        public static final String TIMESTAMP = "timestamp";
        public static final String SYNC_STATE = "sync_state";
        public static final String DELETE_STATE = "delete_state";
    }

    public static class Transaction
    {
        public static final String LIST = "transactions";
        // -------------------------------------------------------------------------------------------------------------
        public static final String ID = "id";
        public static final String SERVER_ID = "server_id";
        public static final String ACCOUNT_FROM_ID = "account_from_id";
        public static final String ACCOUNT_TO_ID = "account_to_id";
        public static final String CATEGORY_ID = "category_id";
        public static final String DATE = "date";
        public static final String AMOUNT = "amount";
        public static final String NOTE = "note";
        public static final String EXCHANGE_RATE = "exchange_rate";
        public static final String STATE = "state";
        public static final String SHOW_IN_TOTALS = "show_in_totals";
        public static final String TIMESTAMP = "timestamp";
        public static final String SYNC_STATE = "sync_state";
        public static final String DELETE_STATE = "delete_state";
    }
}