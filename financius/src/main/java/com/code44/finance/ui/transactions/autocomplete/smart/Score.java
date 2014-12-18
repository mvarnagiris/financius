package com.code44.finance.ui.transactions.autocomplete.smart;

import android.text.format.DateUtils;

import com.code44.finance.data.model.Transaction;
import com.code44.finance.ui.transactions.autocomplete.FinderScore;

import org.joda.time.DateTime;

class Score implements FinderScore {
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

    public Score(long date, long amount) {
        this.currentDateTime = new DateTime(date);
        this.amount = amount;
    }

    @Override public void add(Transaction transaction) {
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

    @Override public float getScore() {
        float score = 0.0f;

        if (hasSameTimeOfDay) {
            score += SCORE_SAME_TIME_OF_DAY;
        }

        if (hasSameDayOfWeek) {
            score += SCORE_SAME_DAY_OF_WEEK;
        }

        if (hasSameDayOfMonth) {
            score += SCORE_SAME_DAY_OF_MONTH;
        }

        if (hasSameAmountSameTimeOfDay) {
            score += SCORE_SAME_AMOUNT_SAME_TIME_OF_DAY;
        }

        if (hasSameAmountSameDayOfWeek) {
            score += SCORE_SAME_AMOUNT_SAME_DAY_OF_WEEK;
        }

        if (hasSameAmountSameDayOfMonth) {
            score += SCORE_SAME_AMOUNT_SAME_DAY_OF_MONTH;
        }

        if (hasSimilarAmountSameTimeOfDay) {
            score += SCORE_SIMILAR_AMOUNT_SAME_TIME_OF_DAY;
        }

        if (hasSimilarAmountSameDayOfWeek) {
            score += SCORE_SIMILAR_AMOUNT_SAME_DAY_OF_WEEK;
        }

        if (hasSimilarAmountSameDayOfMonth) {
            score += SCORE_SIMILAR_AMOUNT_SAME_DAY_OF_MONTH;
        }

        if (hasSameAmount) {
            score += SCORE_SAME_AMOUNT;
        }

        if (hasSimilarAmount) {
            score += SCORE_SIMILAR_AMOUNT;
        }

        return score;
    }
}