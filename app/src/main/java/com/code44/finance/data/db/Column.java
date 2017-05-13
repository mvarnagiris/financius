package com.code44.finance.data.db;

import com.google.common.base.Strings;

public final class Column {
    private final String tableName;
    private final String name;
    private final DataType dataType;
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
    }

    @Override public String toString() {
        return name;
    }

    /**
     * @return Name of the table.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @return Name of the column. Usually column names are prefixed with table name. That depends
     * if {@link #Column(String, String, com.code44.finance.data.db.Column.DataType, String, boolean)}
     * {@code boolean} is {@code true} or any other constructor was used.
     */
    public String getName() {
        return name;
    }

    /**
     * @param prefix Prefix to add to current name. Can be {@code null}.
     * @return Name prefixed with {@code prefix}. If {@code prefix} is {@code null}, then it's the
     * same as using {@link #getName()}.
     */
    public String getName(String prefix) {
        if (Strings.isNullOrEmpty(prefix)) {
            return getName();
        } else {
            return prefix + "_" + name;
        }
    }

    /**
     * @return [table name].[name]
     */
    public String getNameWithTable() {
        return getNameWithTable(tableName);
    }

    /**
     * @param tableName Table to use as qualifier.
     * @return [table name].[name]
     */
    public String getNameWithTable(String tableName) {
        return tableName + "." + name;
    }

    /**
     * @param tableName Table to use as qualifier.
     * @return [table name].[name] as [table name]_[name]
     */
    public String getNameWithAs(String tableName) {
        return getNameWithTable(tableName) + " as " + getName(tableName);
    }

    public String getCreateScript() {
        return name + " " + dataType + (Strings.isNullOrEmpty(defaultValue) ? "" : " default " + defaultValue);
    }

    public enum DataType {
        INTEGER_PRIMARY_KEY("integer primary key autoincrement"),
        TEXT("text"),
        INTEGER("integer"),
        REAL("real"),
        BOOLEAN("boolean"),
        DATETIME("datetime");

        final private String dataType;

        DataType(String dataType) {
            this.dataType = dataType;
        }

        @Override public String toString() {
            return dataType;
        }
    }
}
