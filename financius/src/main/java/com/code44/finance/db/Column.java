package com.code44.finance.db;

import android.text.TextUtils;

public final class Column {
    private final String tableName;
    private final String name;
    private final DataType dataType;
    private final String nameWithTable;
    private final String defaultValue;

    public Column(String tableName, String name, DataType dataType) {
        this(tableName, name, dataType, null);
    }

    public Column(String tableName, String name, DataType dataType, String defaultValue) {
        this(tableName, name, dataType, defaultValue, true);
    }

    public Column(String tableName, String name, DataType dataType, String defaultValue, boolean prefixTableToName) {
        this.tableName = tableName;
        this.name = prefixTableToName ? tableName + "_" + name : name;
        this.dataType = dataType;
        this.defaultValue = defaultValue;
        this.nameWithTable = tableName + "." + name;
    }

    public static String makeColumnCreateScript(Column column) {
        return column.name + " " + column.dataType + (TextUtils.isEmpty(column.defaultValue) ? "" : " default " + column.defaultValue);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getTableName() {
        return tableName;
    }

    public String getName() {
        return name;
    }

    public String getNameWithTable() {
        return nameWithTable;
    }

    public static enum DataType {
        INTEGER_PRIMARY_KEY("integer primary key autoincrement"),
        TEXT("text"),
        INTEGER("integer"),
        REAL("real"),
        BOOLEAN("boolean"),
        DATETIME("datetime");

        final private String dataType;

        private DataType(String dataType) {
            this.dataType = dataType;
        }

        @Override
        public String toString() {
            return dataType;
        }
    }
}
