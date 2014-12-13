package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.code44.finance.common.model.TransactionType;
import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.utils.IOUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagsFinder extends Finder<Category> {
    private final long date;
    private final Account accountFrom;
    private final Account accountTo;
    private final Category category;
    private final List<Tag> tags;
    private final String note;

    protected TagsFinder(Context context, TransactionType transactionType, long date, Long amount, Account accountFrom, Account accountTo, Category category, List<Tag> tags, String note) {
        super(context, transactionType);
        this.date = date;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.category = category;
        this.tags = tags;
        this.note = note;
    }

    @Override public List<Category> find() {
        final Cursor cursor = query();
        if (cursor == null || !cursor.moveToFirst()) {
            return Collections.emptyList();
        }

        final Map<Category, Score> categoryScores = new HashMap<>();
        final Score maxScore = calculateCategoryScoresAndGetMaxScore(cursor, categoryScores);
        IOUtils.closeQuietly(cursor);

        return getSortedCategories(categoryScores, maxScore);
    }

    private Score calculateCategoryScoresAndGetMaxScore(Cursor cursor, Map<Category, Score> categoryScores) {
        int maxSameTimeOfDayCount = 0;
        int maxSameDayOfWeekCount = 0;
        int maxSameDayOfMonthCount = 0;
        long latestTransactionDate = 0;
        do {
            final Transaction transaction = Transaction.from(cursor);
            if (transaction.getCategory() == null) {
                continue;
            }

            final Score score = score(categoryScores, transaction);
            maxSameTimeOfDayCount = Math.max(score.sameTimeOfDayCount, maxSameTimeOfDayCount);
            maxSameDayOfWeekCount = Math.max(score.sameDayOfWeekCount, maxSameDayOfWeekCount);
            maxSameDayOfMonthCount = Math.max(score.sameDayOfMonthCount, maxSameDayOfMonthCount);
            latestTransactionDate = Math.max(score.lastTransactionDate, latestTransactionDate);
        } while (cursor.moveToNext());

        final Score score = new Score();
        score.sameTimeOfDayCount = maxSameTimeOfDayCount;
        score.sameDayOfWeekCount = maxSameDayOfWeekCount;
        score.sameDayOfMonthCount = maxSameDayOfMonthCount;
        score.lastTransactionDate = latestTransactionDate;
        return score;
    }

    private List<Category> getSortedCategories(final Map<Category, Score> categoryScores, final Score maxScore) {
        final List<Category> categories = new ArrayList<>(categoryScores.keySet());
        Collections.sort(categories, new ScoreComparator(categoryScores, maxScore));
        return categories;
    }

    private Score score(Map<Category, Score> categoryScores, Transaction transaction) {
        Score score = categoryScores.get(transaction.getCategory());
        if (score == null) {
            score = new Score();
            categoryScores.put(transaction.getCategory(), score);
        }
        score.add(transaction);
        return score;
    }

    private Cursor query() {
        final Query query = getBaseQuery();

        if (!Strings.isEmpty(note)) {
            query.selection(" and " + Tables.Transactions.NOTE + "=?", note);
        }

        if (tags != null && tags.size() > 0) {
            final List<String> tagIds = new ArrayList<>();
            for (Tag tag : tags) {
                tagIds.add(tag.getId());
            }
            query.selection(" and ").selectionInClause(Tables.TransactionTags.TAG_ID.getName(), tagIds);
        }

        if (category != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "<>?", category.getId());
        }

        if (accountFrom != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_FROM_ID + "=?", accountFrom.getId());
        }

        if (accountTo != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_TO_ID + "=?", accountTo.getId());
        }

        return query(query);
    }

    private static class ScoreComparator implements Comparator<Category> {
        private final Map<Category, Score> categoryScores;
        private final Score maxScore;

        private ScoreComparator(Map<Category, Score> categoryScores, Score maxScore) {
            this.categoryScores = categoryScores;
            this.maxScore = maxScore;
        }

        @Override public int compare(Category lhs, Category rhs) {
            final float leftScore = categoryScores.get(lhs).getScore(maxScore);
            final float rightScore = categoryScores.get(rhs).getScore(maxScore);
            return Float.compare(leftScore, rightScore);
        }
    }

    private class Score {
        private static final long MAX_TIME_OF_DAY_DELTA = DateUtils.HOUR_IN_MILLIS;

        private static final float WEIGHT_SAME_TIME_OF_DAY = 0.5f;
        private static final float WEIGHT_SAME_DAY_OF_WEEK = 0.25f;
        private static final float WEIGHT_SAME_DAY_OF_MONTH = 0.1f;
        private static final float WEIGHT_LAST_TRANSACTION = 0.15f;

        private final DateTime currentDateTime = new DateTime(date);

        private int sameTimeOfDayCount = 0;
        private int sameDayOfWeekCount = 0;
        private int sameDayOfMonthCount = 0;
        private long lastTransactionDate = 0;

        private float scoreCache = -1;

        public void add(Transaction transaction) {
            final DateTime dateTime = new DateTime(transaction.getDate());

            if (Math.abs(dateTime.getMillisOfDay() - currentDateTime.getMillisOfDay()) <= MAX_TIME_OF_DAY_DELTA) {
                sameTimeOfDayCount++;
            }

            if (dateTime.getDayOfWeek() == currentDateTime.getDayOfWeek()) {
                sameDayOfWeekCount++;
            }

            if (dateTime.getDayOfMonth() == currentDateTime.getDayOfMonth()) {
                sameDayOfMonthCount++;
            }

            if (transaction.getDate() > lastTransactionDate) {
                lastTransactionDate = transaction.getDate();
            }

            scoreCache = -1;
        }

        public float getScore(Score maxScore) {
            if (scoreCache >= 0) {
                return scoreCache;
            }

            final float sameTimeOfDayScore = maxScore.sameTimeOfDayCount > 0 ? WEIGHT_SAME_TIME_OF_DAY * sameTimeOfDayCount / maxScore.sameTimeOfDayCount : 0;
            final float sameDayOfWeekScore = maxScore.sameDayOfWeekCount > 0 ? WEIGHT_SAME_DAY_OF_WEEK * sameDayOfWeekCount / maxScore.sameDayOfWeekCount : 0;
            final float sameDayOfMonthScore = maxScore.sameDayOfMonthCount > 0 ? WEIGHT_SAME_DAY_OF_MONTH * sameDayOfMonthCount / maxScore.sameDayOfMonthCount : 0;
            final float lastTransactionDateScore = lastTransactionDate == maxScore.lastTransactionDate ? WEIGHT_LAST_TRANSACTION : 0;

            return sameTimeOfDayScore + sameDayOfWeekScore + sameDayOfMonthScore + lastTransactionDateScore;
        }
    }
}
