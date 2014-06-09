package com.code44.finance.db;

import android.provider.BaseColumns;

public final class Tables {
    private Tables() {
    }

    private static Column getIdColumn(String tableName) {
        return new Column(tableName, BaseColumns._ID, Column.DataType.INTEGER_PRIMARY_KEY, null, false);
    }

    private static String makeCreateScript(String table, Column... columns) {
        final StringBuilder sb = new StringBuilder("create table ");
        sb.append(table);
        sb.append(" (");

        if (columns != null) {
            for (int i = 0, size = columns.length; i < size; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(Column.makeColumnCreateScript(columns[i]));
            }
        }

        sb.append(");");

        return sb.toString();
    }

    public static final class Currencies {
        public static final String TABLE_NAME = "currencies";

        public static final Column ID = getIdColumn(TABLE_NAME);
        public static final Column CODE = new Column(TABLE_NAME, "code", Column.DataType.TEXT);
        public static final Column SYMBOL = new Column(TABLE_NAME, "symbol", Column.DataType.TEXT);
        public static final Column SYMBOL_POSITION = new Column(TABLE_NAME, "symbol_position", Column.DataType.INTEGER);
        public static final Column DECIMAL_SEPARATOR = new Column(TABLE_NAME, "decimal_separator", Column.DataType.TEXT);
        public static final Column GROUP_SEPARATOR = new Column(TABLE_NAME, "group_separator", Column.DataType.TEXT);
        public static final Column DECIMAL_COUNT = new Column(TABLE_NAME, "decimal_count", Column.DataType.INTEGER);
        public static final Column IS_DEFAULT = new Column(TABLE_NAME, "is_default", Column.DataType.BOOLEAN);
        public static final Column EXCHANGE_RATE = new Column(TABLE_NAME, "exchange_rate", Column.DataType.REAL);

        public static final String[] PROJECTION = {CODE.getName(), SYMBOL.getName(), SYMBOL_POSITION.getName(),
                DECIMAL_SEPARATOR.getName(), GROUP_SEPARATOR.getName(), DECIMAL_COUNT.getName(),
                IS_DEFAULT.getName(), EXCHANGE_RATE.getName()};

        private Currencies() {
        }

        public static String createScript() {
            return makeCreateScript(TABLE_NAME, ID, CODE, SYMBOL, SYMBOL_POSITION, DECIMAL_SEPARATOR,
                    GROUP_SEPARATOR, DECIMAL_COUNT, IS_DEFAULT, EXCHANGE_RATE);
        }
    }
}
