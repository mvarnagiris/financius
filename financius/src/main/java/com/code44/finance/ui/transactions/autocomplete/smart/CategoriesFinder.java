package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.Pair;
import android.text.format.DateUtils;

import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.utils.IOUtils;
import com.code44.finance.utils.LogUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFinder extends Finder<Category> {
    protected CategoriesFinder(Context context, AutoCompleteInput autoCompleteInput) {
        super(context, autoCompleteInput);
    }

    @Override protected Pair<Category, List<Category>> find(AutoCompleteInput input) {
        final Cursor cursor = query(input);
        if (cursor == null || !cursor.moveToFirst()) {
            return Pair.create(null, Collections.<Category>emptyList());
        }

        final Map<Category, Score> categoryScores = new HashMap<>();
        final Score maxScore = calculateCategoryScoresAndGetMaxScore(cursor, categoryScores, input);
        IOUtils.closeQuietly(cursor);

        final List<Category> sortedCategories = getSortedCategories(categoryScores, maxScore);

        LogUtils.d(CategoriesFinder.class.getSimpleName(), "Categories scores-----------------------------------------");
        for (Category category : sortedCategories) {
            LogUtils.d(CategoriesFinder.class.getSimpleName(), category.getTitle() + ": " + categoryScores.get(category).scoreCache);
        }

        return getResult(sortedCategories);
    }

    private Score calculateCategoryScoresAndGetMaxScore(Cursor cursor, Map<Category, Score> categoryScores, AutoCompleteInput input) {
        int maxSameTimeOfDayCount = 0;
        int maxSameDayOfWeekCount = 0;
        int maxSameDayOfMonthCount = 0;
        long latestTransactionDate = 0;
        do {
            final Transaction transaction = Transaction.from(cursor);
            if (transaction.getCategory() == null) {
                continue;
            }

            final Score score = score(categoryScores, transaction, input);
            maxSameTimeOfDayCount = Math.max(score.sameTimeOfDayCount, maxSameTimeOfDayCount);
            maxSameDayOfWeekCount = Math.max(score.sameDayOfWeekCount, maxSameDayOfWeekCount);
            maxSameDayOfMonthCount = Math.max(score.sameDayOfMonthCount, maxSameDayOfMonthCount);
            latestTransactionDate = Math.max(score.lastTransactionDate, latestTransactionDate);
        } while (cursor.moveToNext());

        final Score score = new Score(0, 0);
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

    private Pair<Category, List<Category>> getResult(List<Category> sortedCategories) {
        final Category bestMatch;
        if (sortedCategories.size() > 0) {
            bestMatch = sortedCategories.remove(0);
        } else {
            bestMatch = null;
        }

        return Pair.create(bestMatch, sortedCategories);
    }

    private Score score(Map<Category, Score> categoryScores, Transaction transaction, AutoCompleteInput input) {
        Score score = categoryScores.get(transaction.getCategory());
        if (score == null) {
            score = new Score(input.getDate(), input.getAmount());
            categoryScores.put(transaction.getCategory(), score);
        }
        score.add(transaction);
        return score;
    }

    private Cursor query(AutoCompleteInput input) {
        final Query query = getBaseQuery();

        if (!Strings.isEmpty(input.getNote())) {
            query.selection(" and " + Tables.Transactions.NOTE + "=?", input.getNote());
        }

        if (input.getTags() != null && input.getTags().size() > 0) {
            final List<String> tagIds = new ArrayList<>();
            for (Tag tag : input.getTags()) {
                tagIds.add(tag.getId());
            }
            query.selection(" and ").selectionInClause(Tables.TransactionTags.TAG_ID.getName(), tagIds);
        }

        if (input.getCategory() != null) {
            query.selection(" and " + Tables.Transactions.CATEGORY_ID + "=?", input.getCategory().getId());
        }

        if (input.getAccountFrom() != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_FROM_ID + "=?", input.getAccountFrom().getId());
        }

        if (input.getAccountTo() != null) {
            query.selection(" and " + Tables.Transactions.ACCOUNT_TO_ID + "=?", input.getAccountTo().getId());
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
            return Float.compare(rightScore, leftScore);
        }
    }

    private class Score {
        private static final long MAX_TIME_OF_DAY_DELTA = DateUtils.HOUR_IN_MILLIS;
        private static final long MAX_SIMILAR_AMOUNT_DELTA = 5000;

        private static final float WEIGHT_SAME_TIME_OF_DAY = 0.4f;
        private static final float WEIGHT_SIMILAR_AMOUNT = 0.1f;
        private static final float WEIGHT_SAME_DAY_OF_WEEK = 0.25f;
        private static final float WEIGHT_SAME_DAY_OF_MONTH = 0.1f;
        private static final float WEIGHT_LAST_TRANSACTION = 0.15f;

        private final DateTime currentDateTime;
        private final long amount;

        private int sameTimeOfDayCount = 0;
        private int sameDayOfWeekCount = 0;
        private int sameDayOfMonthCount = 0;
        private int sameAmountSameTimeOfDayCount = 0;
        private int sameAmountSameDayOfWeekCount = 0;
        private int sameAmountSameDayOfMonthCount = 0;
        private int similarAmountSameTimeOfDayCount = 0;
        private int similarAmountSameDayOfWeekCount = 0;
        private int similarAmountSameDayOfMonthCount = 0;
        private long lastTransactionDate = 0;

        private float scoreCache = -1;

        private Score(long date, long amount) {
            this.currentDateTime = new DateTime(date);
            this.amount = amount;
        }

        public void add(Transaction transaction) {
            final DateTime dateTime = new DateTime(transaction.getDate());
            final boolean isSameTimeOfDay = Math.abs(dateTime.getMillisOfDay() - currentDateTime.getMillisOfDay()) <= MAX_TIME_OF_DAY_DELTA;
            final boolean isSameDayOfWeek = dateTime.getDayOfWeek() == currentDateTime.getDayOfWeek();
            final boolean isSameDayOfMonth = dateTime.getDayOfMonth() == currentDateTime.getDayOfMonth();
            final boolean isSameAmount = amount > 0 && amount == transaction.getAmount();
            final boolean isSimilarAmount = amount > 0 && Math.abs(amount - transaction.getAmount()) < MAX_SIMILAR_AMOUNT_DELTA;

            if (isSameTimeOfDay) {
                sameTimeOfDayCount++;
                if (isSameAmount) {
                    sameAmountSameTimeOfDayCount++;
                    similarAmountSameTimeOfDayCount++;
                } else if (isSimilarAmount) {
                    similarAmountSameTimeOfDayCount++;
                }
            }

            if (isSameDayOfWeek) {
                sameDayOfWeekCount++;
                if (isSameAmount) {
                    sameAmountSameDayOfWeekCount++;
                    similarAmountSameDayOfWeekCount++;
                } else if (isSimilarAmount) {
                    similarAmountSameDayOfWeekCount++;
                }
            }

            if (isSameDayOfMonth) {
                sameDayOfMonthCount++;
                if (isSameAmount) {
                    sameAmountSameDayOfMonthCount++;
                    similarAmountSameDayOfMonthCount++;
                } else if (isSimilarAmount) {
                    similarAmountSameDayOfMonthCount++;
                }
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

            if (sameAmountSameTimeOfDayCount > 0 || sameAmountSameDayOfWeekCount > 0 || sameAmountSameDayOfMonthCount > 0) {
                scoreCache = 1f;
                return scoreCache;
            }

            final float sameTimeOfDayScore = maxScore.sameTimeOfDayCount > 0 ? WEIGHT_SAME_TIME_OF_DAY * sameTimeOfDayCount / maxScore.sameTimeOfDayCount : 0;
            final float sameDayOfWeekScore = maxScore.sameDayOfWeekCount > 0 ? WEIGHT_SAME_DAY_OF_WEEK * sameDayOfWeekCount / maxScore.sameDayOfWeekCount : 0;
            final float sameDayOfMonthScore = maxScore.sameDayOfMonthCount > 0 ? WEIGHT_SAME_DAY_OF_MONTH * sameDayOfMonthCount / maxScore.sameDayOfMonthCount : 0;
            final float similarAmountSameTimeOfDayScore = maxScore.similarAmountSameDayOfMonthCount > 0 ? WEIGHT_SIMILAR_AMOUNT / 3 * similarAmountSameTimeOfDayCount / maxScore.similarAmountSameTimeOfDayCount : 0;
            final float similarAmountSameDayOfWeekScore = maxScore.similarAmountSameDayOfWeekCount > 0 ? WEIGHT_SIMILAR_AMOUNT / 3 * similarAmountSameDayOfWeekCount / maxScore.similarAmountSameDayOfWeekCount : 0;
            final float similarAmountSameDayOfMonthScore = maxScore.similarAmountSameDayOfMonthCount > 0 ? WEIGHT_SIMILAR_AMOUNT / 3 * similarAmountSameDayOfMonthCount / maxScore.similarAmountSameDayOfMonthCount : 0;
            final float lastTransactionDateScore = lastTransactionDate == maxScore.lastTransactionDate ? WEIGHT_LAST_TRANSACTION : 0;

            scoreCache = sameTimeOfDayScore + sameDayOfWeekScore + sameDayOfMonthScore + similarAmountSameTimeOfDayScore + similarAmountSameDayOfWeekScore + similarAmountSameDayOfMonthScore + lastTransactionDateScore;
            return scoreCache;
        }
    }
}
