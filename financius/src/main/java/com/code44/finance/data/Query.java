package com.code44.finance.data;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.code44.finance.data.db.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Query {
    private final Set<String> projection = new HashSet<>();
    private final List<String> selection = new ArrayList<>();
    private final List<String> selectionArgs = new ArrayList<>();
    private final List<String> sortOrder = new ArrayList<>();

    private Query() {
    }

    public static Query create() {
        return new Query();
    }

    public String[] getProjection() {
        if (projection.size() == 0) {
            return null;
        }
        return projection.toArray(new String[projection.size()]);
    }

    public String getSelection() {
        if (selection.size() == 0) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        for (String clause : selection) {
            sb.append(" ").append(clause);
        }

        return sb.toString();
    }

    public String[] getSelectionArgs() {
        if (selectionArgs.size() == 0) {
            return null;
        }
        return selectionArgs.toArray(new String[selectionArgs.size()]);
    }

    public String getSortOrder() {
        if (sortOrder.size() == 0) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        for (String order : sortOrder) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(order);
        }

        return sb.toString();
    }

    public CursorLoader asCursorLoader(Context context, Uri uri) {
        return new CursorLoader(context, uri, getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
    }

    public ContentProviderQuery from(Context context, Uri uri) {
        return new ContentProviderQuery(this, context, uri);
    }

    public DatabaseQuery from(SQLiteDatabase database, String table) {
        return new DatabaseQuery(this, database, table);
    }

    public Query projectionId(Column idColumn) {
        projection(idColumn.getNameWithTable());
        return this;
    }

    public Query projection(String column) {
        projection.add(column);
        return this;
    }

    public Query projection(String... columns) {
        return projection(Arrays.asList(columns));
    }

    public Query projection(List<String> columns) {
        projection.addAll(columns);
        return this;
    }

    public Query selection(String clause) {
        selection.add(clause);
        return this;
    }

    public Query selection(String clause, String... args) {
        selection(clause);
        args(args);
        return this;
    }

    public Query selectionInClause(String clause, int count) {
        return selection(makeInClause(clause, count));
    }

    public Query selectionInClause(String clause, int count, String... args) {
        selection(makeInClause(clause, count));
        args(args);
        return this;
    }

    public Query selectionInClause(String clause, List<String> ids) {
        selection(makeInClause(clause, ids.size()));
        for (String id : ids) {
            args(id);
        }
        return this;
    }

    public Query args(String arg) {
        selectionArgs.add(arg);
        return this;
    }

    public Query args(String... args) {
        return args(Arrays.asList(args));
    }

    public Query args(List<String> args) {
        selectionArgs.addAll(args);
        return this;
    }

    public Query sortOrder(String order) {
        sortOrder.add(order);
        return this;
    }

    public Query sortOrder(String... orders) {
        return sortOrder(Arrays.asList(orders));
    }

    public Query sortOrder(List<String> orders) {
        sortOrder.addAll(orders);
        return this;
    }

    private String makeInClause(String value, int count) {
        final StringBuilder sb = new StringBuilder();
        sb.append(value).append(" in (");
        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    public static class ContentProviderQuery {
        private final Query query;
        private final Context context;
        private final Uri uri;

        private ContentProviderQuery(Query query, Context context, Uri uri) {
            this.query = query;
            this.context = context;
            this.uri = uri;
        }

        public Cursor execute() {
            return context.getContentResolver().query(uri, query.getProjection(), query.getSelection(), query.getSelectionArgs(), query.getSortOrder());
        }
    }

    public static class DatabaseQuery {
        private final Query query;
        private final SQLiteDatabase database;
        private final List<String> tables;

        private DatabaseQuery(Query query, SQLiteDatabase database, String table) {
            this.query = query;
            this.database = database;
            this.tables = new ArrayList<>();
            tables.add(table);
        }

        public String getTables() {
            return TextUtils.join("", tables);
        }

        public DatabaseQuery innerJoin(String table, String on) {
            tables.add(" inner join " + table + " on (" + on + ")");
            return this;
        }

        public Cursor execute() {
            String tables = getTables();
            String[] projection = query.getProjection();
            String selection = query.getSelection();
            String[] selectionArgs = query.getSelectionArgs();
            String sortOrder = query.getSortOrder();
            Cursor cursor = database.query(tables, projection, selection, selectionArgs, null, null, sortOrder);
            cursor.moveToFirst();
            return cursor;
        }
    }

}
