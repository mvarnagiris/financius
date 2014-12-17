package com.code44.finance.ui.transactions.autocomplete.smart;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.Pair;
import android.text.format.DateUtils;
import android.util.Log;

import com.code44.finance.common.utils.Strings;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;
import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteInput;
import com.code44.finance.utils.IOUtils;

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
        calculateCategoryScores(cursor, categoryScores, input);
        IOUtils.closeQuietly(cursor);

        final List<Category> sortedCategories = getSortedCategories(categoryScores);

        Log.d(CategoriesFinder.class.getSimpleName(), "Categories scores-----------------------------------------");
        for (Category category : sortedCategories) {
            Log.d(CategoriesFinder.class.getSimpleName(), category.getTitle() + ": " + categoryScores.get(category).getScore());
        }

        return getResult(sortedCategories);
    }

    private void calculateCategoryScores(Cursor cursor, Map<Category, Score> categoryScores, AutoCompleteInput input) {
        do {
            final Transaction transaction = Transaction.from(cursor);
            if (transaction.getCategory() == null) {
                continue;
            }

            score(categoryScores, transaction, input);
        } while (cursor.moveToNext());
    }

    private List<Category> getSortedCategories(final Map<Category, Score> categoryScores) {
        final List<Category> categories = new ArrayList<>(categoryScores.keySet());
        Collections.sort(categories, new ScoreComparator(categoryScores));
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

    private void score(Map<Category, Score> categoryScores, Transaction transaction, AutoCompleteInput input) {
        Score score = categoryScores.get(transaction.getCategory());
        if (score == null) {
            score = new Score(input.getDate(), input.getAmount());
            categoryScores.put(transaction.getCategory(), score);
        }
        score.add(transaction);
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

        private ScoreComparator(Map<Category, Score> categoryScores) {
            this.categoryScores = categoryScores;
        }

        @Override public int compare(Category lhs, Category rhs) {
            final float leftScore = categoryScores.get(lhs).getScore();
            final float rightScore = categoryScores.get(rhs).getScore();
            return Float.compare(rightScore, leftScore);
        }
    }

    private class Score {
        private static final long MAX_TIME_OF_DAY_DELTA = DateUtils.MINUTE_IN_MILLIS * 45;
        private static final long MAX_SIMILAR_AMOUNT_DELTA = 5000;

        private static final float SCORE_SAME_AMOUNT_SAME_TIME_OF_DAY = 20;
        private static final float SCORE_SAME_AMOUNT_SAME_DAY_OF_MONTH = 20;
        private static final float SCORE_SAME_AMOUNT_SAME_DAY_OF_WEEK = 20;
        private static final float SCORE_SAME_AMOUNT = 10;
        private static final float SCORE_SIMILAR_AMOUNT_SAME_TIME_OF_DAY = 5;
        private static final float SCORE_SIMILAR_AMOUNT_SAME_DAY_OF_WEEK = 5;
        private static final float SCORE_SIMILAR_AMOUNT_SAME_DAY_OF_MONTH = 5;
        private static final float SCORE_SIMILAR_AMOUNT = 5;
        private static final float SCORE_SAME_TIME_OF_DAY = 5;
        private static final float SCORE_SAME_DAY_OF_WEEK = 5;
        private static final float SCORE_SAME_DAY_OF_MONTH = 5;

        private final DateTime currentDateTime;
        private final long amount;

        private boolean hasSameTimeOfDay = false;
        private boolean hasSameDayOfWeek = false;
        private boolean hasSameDayOfMonth = false;
        private boolean hasSameAmountSameTimeOfDay = false;
        private boolean hasSameAmountSameDayOfWeek = false;
        private boolean hasSameAmountSameDayOfMonth = false;
        private boolean hasSimilarAmountSameTimeOfDay = false;
        private boolean hasSimilarAmountSameDayOfWeek = false;
        private boolean hasSimilarAmountSameDayOfMonth = false;
        private boolean hasSameAmount = false;
        private boolean hasSimilarAmount = false;

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

            if (isSameAmount) {
                hasSameAmount = true;
                hasSimilarAmount = true;
            } else if (isSimilarAmount) {
                hasSimilarAmount = true;
            }

            if (isSameTimeOfDay) {
                hasSameTimeOfDay = true;
                if (isSameAmount) {
                    hasSameAmountSameTimeOfDay = true;
                    hasSimilarAmountSameTimeOfDay = true;
                } else if (isSimilarAmount) {
                    hasSimilarAmountSameTimeOfDay = true;
                }
            }

            if (isSameDayOfWeek) {
                hasSameDayOfWeek = true;
                if (isSameAmount) {
                    hasSameAmountSameDayOfWeek = true;
                    hasSimilarAmountSameDayOfWeek = true;
                } else if (isSimilarAmount) {
                    hasSimilarAmountSameDayOfWeek = true;
                }
            }

            if (isSameDayOfMonth) {
                hasSameDayOfMonth = true;
                if (isSameAmount) {
                    hasSameAmountSameDayOfMonth = true;
                    hasSimilarAmountSameDayOfMonth = true;
                } else if (isSimilarAmount) {
                    hasSimilarAmountSameDayOfMonth = true;
                }
            }
        }

        public float getScore() {
            float score = 0.0f;
            score += hasSameTimeOfDay ? SCORE_SAME_TIME_OF_DAY : 0;
            score += hasSameDayOfWeek ? SCORE_SAME_DAY_OF_WEEK : 0;
            score += hasSameDayOfMonth ? SCORE_SAME_DAY_OF_MONTH : 0;
            score += hasSameAmountSameTimeOfDay ? SCORE_SAME_AMOUNT_SAME_TIME_OF_DAY : 0;
            score += hasSameAmountSameDayOfWeek ? SCORE_SAME_AMOUNT_SAME_DAY_OF_WEEK : 0;
            score += hasSameAmountSameDayOfMonth ? SCORE_SAME_AMOUNT_SAME_DAY_OF_MONTH : 0;
            score += hasSimilarAmountSameTimeOfDay ? SCORE_SIMILAR_AMOUNT_SAME_TIME_OF_DAY : 0;
            score += hasSimilarAmountSameDayOfWeek ? SCORE_SIMILAR_AMOUNT_SAME_DAY_OF_WEEK : 0;
            score += hasSimilarAmountSameDayOfMonth ? SCORE_SIMILAR_AMOUNT_SAME_DAY_OF_MONTH : 0;
            score += hasSameAmount ? SCORE_SAME_AMOUNT : 0;
            score += hasSimilarAmount ? SCORE_SIMILAR_AMOUNT : 0;
            return score;
        }
    }
}
