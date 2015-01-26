package com.code44.finance.ui.reports.trends;

import com.code44.finance.data.model.CurrencyFormat;
import com.code44.finance.money.AmountGrouper;

import lecho.lib.hellocharts.model.Line;

public class DefaultTrendsChartPresenter extends TrendsChartPresenter {
    public DefaultTrendsChartPresenter(TrendsChartView trendsChartView, CurrencyFormat mainCurrencyFormat) {
        super(trendsChartView, mainCurrencyFormat);
    }

    @Override protected AmountGrouper.AmountCalculator[] getTransactionValidators() {
//        return new AmountGrouper.AmountCalculator[]{new AmountGrouper.AmountCalculator() {
//            @Override public boolean isTransactionValid(Transaction transaction) {
//                return transaction.includeInReports() && transaction.getTransactionType() != TransactionType.Transfer && transaction.getTransactionState() == TransactionState.Confirmed;
//            }
//        }};
        return null;
    }

    @Override protected void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line) {
    }
}
