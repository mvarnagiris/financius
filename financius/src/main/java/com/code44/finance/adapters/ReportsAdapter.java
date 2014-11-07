package com.code44.finance.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.graphs.pie.PieChartData;
import com.code44.finance.ui.reports.categories.CategoriesReportView;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private final Context context;

    private PieChartData pieChartData;

    public ReportsAdapter(final Context context) {
        this.context = context;
    }

    @Override public ViewHolder onCreateViewHolder(final ViewGroup parent, final int position) {
        final CardView cardView = new CardView(context);
        cardView.setRadius(context.getResources().getDimension(R.dimen.card_radius));
        final int padding = context.getResources().getDimensionPixelSize(R.dimen.space_normal);
        cardView.setPadding(padding, padding, padding, padding);
        switch (position) {
            case 0:
            case 1:
//                cardView.addView(new CategoriesReportView(context));
                break;
            default:
                throw new IllegalArgumentException("Position " + position + " is not supported.");
        }
        return new ViewHolder(cardView);
    }

    @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CategoriesReportView categoriesReportView = (CategoriesReportView) ((CardView) holder.itemView).getChildAt(0);
        categoriesReportView.setPieChartData(pieChartData);
        categoriesReportView.setTotalExpense(pieChartData == null ? 0 : pieChartData.getTotalValue());
    }

    @Override public int getItemCount() {
        return 2;
    }

    public void setCategoriesReportData(PieChartData pieChartData) {
        this.pieChartData = pieChartData;
        notifyItemChanged(0);
    }

    static final class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(final View itemView) {
            super(itemView);
        }
    }
}
