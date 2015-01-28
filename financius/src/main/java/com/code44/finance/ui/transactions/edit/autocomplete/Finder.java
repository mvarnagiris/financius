package com.code44.finance.ui.transactions.edit.autocomplete;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.common.model.TransactionState;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.data.providers.TransactionsProvider;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Finder<T> {
    private final Context context;
    private final AutoCompleteInput autoCompleteInput;
    private final Logger logger;

    protected Finder(Context context, AutoCompleteInput autoCompleteInput, boolean log) {
        this.context = context;
        this.autoCompleteInput = autoCompleteInput;
        this.logger = Logger.with(getClass().getSimpleName()).logLevel(log ? Logger.LogLevel.Debug : Logger.LogLevel.Warning);
    }

    public List<T> find() {
        final Cursor cursor = queryTransactions(autoCompleteInput);
        if (cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        final Map<T, ? extends FinderScore> scores = findScores(cursor);
        IOUtils.closeQuietly(cursor);
        final List<T> sorted = getSorted(scores);
        log(sorted, scores);
        return sorted;
    }

    protected abstract Cursor queryTransactions(AutoCompleteInput input);

    protected abstract FinderScore createScore(AutoCompleteInput autoCompleteInput);

    protected abstract boolean isValidTransaction(Transaction transaction);

    protected abstract T getModelForTransaction(Transaction transaction);

    protected abstract String getLogName(T model);

    protected Query getBaseQuery() {
        final long fromDate = System.currentTimeMillis() - (DateUtils.DAY_IN_MILLIS * 90);
        return Tables.Transactions.getQuery()
                .selection(" and " + Tables.Transactions.TYPE + "=?", autoCompleteInput.getTransactionType().asString())
                .selection(" and " + Tables.Transactions.STATE + "=?", TransactionState.Confirmed.asString())
                .selection(" and " + Tables.Transactions.DATE + ">?", String.valueOf(fromDate));
    }

    protected Cursor executeQuery(Query query) {
        return query.from(context, TransactionsProvider.uriTransactions()).execute();
    }

    private Map<T, FinderScore> findScores(Cursor cursor) {
        final Map<T, FinderScore> scores = new HashMap<>();
        do {
            final Transaction transaction = Transaction.from(cursor);
            if (!isValidTransaction(transaction)) {
                continue;
            }

            score(scores, autoCompleteInput, transaction, getModelForTransaction(transaction));
        } while (cursor.moveToNext());
        return scores;
    }

    private void score(Map<T, FinderScore> scores, AutoCompleteInput autoCompleteInput, Transaction transaction, T model) {
        FinderScore score = scores.get(model);
        if (score == null) {
            score = createScore(autoCompleteInput);
            scores.put(model, score);
        }
        score.add(transaction);
    }

    private List<T> getSorted(final Map<T, ? extends FinderScore> scores) {
        final List<T> sorted = new ArrayList<>(scores.keySet());
        Collections.sort(sorted, new ScoreComparator<>(scores));
        return sorted;
    }

    private void log(List<T> sorted, Map<T, ? extends FinderScore> scores) {
        logger.debug("Start scores -----------------------------------------");
        for (T model : sorted) {
            logger.debug(getLogName(model) + ": " + scores.get(model).getScore());
        }
        logger.debug("End scores -------------------------------------------");
    }

    private static class ScoreComparator<T> implements Comparator<T> {
        private final Map<T, ? extends FinderScore> scores;

        private ScoreComparator(Map<T, ? extends FinderScore> scores) {
            this.scores = scores;
        }

        @Override public int compare(T lhs, T rhs) {
            final float leftScore = scores.get(lhs).getScore();
            final float rightScore = scores.get(rhs).getScore();
            return Float.compare(rightScore, leftScore);
        }
    }
}
