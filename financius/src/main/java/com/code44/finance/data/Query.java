package com.code44.finance.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.code44.finance.data.db.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Query {
    private final String[] projection;
    private final String selection;
    private final String[] selectionArgs;
    private final String sortOrder;

    private Query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
    }

    public static Builder get() {
        return new Builder();
    }

    public String[] getProjection() {
        return projection;
    }

    public String getSelection() {
        return selection;
    }

    public String[] getSelectionArgs() {
        return selectionArgs;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public CursorLoader asCursorLoader(Context context, Uri uri) {
        return new CursorLoader(context, uri, getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
    }

    public Cursor asCursor(Context context, Uri uri) {
        return context.getContentResolver().query(uri, getProjection(), getSelection(), getSelectionArgs(), getSortOrder());
    }

    public static class Builder {
        private final Set<String> projection = new HashSet<>();
        private final List<String> selection = new ArrayList<>();
        private final List<String> selectionArgs = new ArrayList<>();
        private final List<String> sortOrder = new ArrayList<>();

        private Builder() {
        }

        public Builder projectionId(Column idColumn) {
            projection(idColumn.getNameWithTable());
            return this;
        }

        public Builder projection(String column) {
            projection.add(column);
            return this;
        }

        public Builder projection(String... columns) {
            return projection(Arrays.asList(columns));
        }

        public Builder projection(List<String> columns) {
            projection.addAll(columns);
            return this;
        }

        public Builder selection(String clause) {
            selection.add(clause);
            return this;
        }

        public Builder selection(String clause, String... args) {
            selection(clause);
            args(args);
            return this;
        }

        public Builder selectionInClause(String clause, int count) {
            return selection(makeInClause(clause, count));
        }

        public Builder selectionInClause(String clause, int count, String... args) {
            selection(makeInClause(clause, count));
            args(args);
            return this;
        }

        public Builder args(String arg) {
            selectionArgs.add(arg);
            return this;
        }

        public Builder args(String... args) {
            return args(Arrays.asList(args));
        }

        public Builder args(List<String> args) {
            selectionArgs.addAll(args);
            return this;
        }

        public Builder sortOrder(String order) {
            sortOrder.add(order);
            return this;
        }

        public Builder sortOrder(String... orders) {
            return sortOrder(Arrays.asList(orders));
        }

        public Builder sortOrder(List<String> orders) {
            sortOrder.addAll(orders);
            return this;
        }

        public Query build() {
            return new Query(buildProjection(), buildSelection(), buildSelectionArgs(), buildSortOrder());
        }

        private String[] buildProjection() {
            if (projection.size() == 0) {
                return null;
            }
            return projection.toArray(new String[projection.size()]);
        }

        private String buildSelection() {
            if (selection.size() == 0) {
                return null;
            }

            final StringBuilder sb = new StringBuilder();
            for (String clause : selection) {
                sb.append(" ").append(clause);
            }

            return sb.toString();
        }

        private String[] buildSelectionArgs() {
            if (selectionArgs.size() == 0) {
                return null;
            }
            return selectionArgs.toArray(new String[selectionArgs.size()]);
        }

        private String buildSortOrder() {
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
    }
}
