package com.code44.finance.ui.reports.trends;

import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.AmountGrouper;

import lecho.lib.hellocharts.model.Line;

public class DefaultTrendsChartPresenter extends TrendsChartPresenter {
    public DefaultTrendsChartPresenter(TrendsChartView trendsChartView, AmountFormatter amountFormatter) {
        super(trendsChartView, amountFormatter);
    }

    @Override protected AmountGrouper.AmountCalculator[] getTransactionValidators() {
//        return new AmountGrouper.AmountCalculator[]{new AmountGrouper.AmountCalculator() {
//            @Override public long getAmount(Transaction transaction) {
//                if (transaction.includeInReports() && transaction.getTransactionType() != TransactionType.Transfer && transaction.getTransactionState() == TransactionState.Confirmed) {
//                    return AmountRetriever.getExpenseAmount(transaction, )
//                }
//                return 0;
//            }
//        }};
        return null;
    }

    @Override protected void onLineCreated(AmountGrouper.AmountCalculator amountCalculator, Line line) {
    }
}
