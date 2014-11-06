package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Currency;
import com.code44.finance.ui.reports.categories.CategoriesReportData;
import com.code44.finance.utils.MoneyFormatter;

public class CategoriesReportAdapter extends BaseAdapter {
    private final Context context;
    private final Currency defaultCurrency;
    private CategoriesReportData categoriesReportData;
    private long totalAmount = 0;

    public CategoriesReportAdapter(Context context, Currency defaultCurrency) {
        this.context = context;
        this.defaultCurrency = defaultCurrency;
    }

    @Override public int getCount() {
        return categoriesReportData != null ? categoriesReportData.size() : 0;
    }

    @Override public Object getItem(int position) {
        return categoriesReportData.get(position);
    }

    @Override public long getItemId(int position) {
        return categoriesReportData.get(position).getCategory().getLocalId();
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.li_category_report, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CategoriesReportData.CategoriesReportItem item = categoriesReportData.get(position);
        holder.color_IV.setColorFilter(item.getCategory().getColor());
        final int percent = getPercent(item.getAmount());
        holder.percent_TV.setText((percent == 0 ? "<1" : (percent == 100 && getCount() > 1 ? ">99" : percent)) + "%");
        holder.title_TV.setText(item.getCategory().getTitle());
        holder.amount_TV.setText(MoneyFormatter.format(defaultCurrency, item.getAmount()));

        return convertView;
    }

    public void setData(CategoriesReportData categoriesReportData, long totalAmount) {
        this.categoriesReportData = categoriesReportData;
        this.totalAmount = totalAmount;
        notifyDataSetChanged();
    }

    private int getPercent(long amount) {
        return Math.round(100.0f * amount / totalAmount);
    }

    private static final class ViewHolder {
        public final ImageView color_IV;
        public final TextView percent_TV;
        public final TextView title_TV;
        public final TextView amount_TV;

        public ViewHolder(View view) {
            color_IV = (ImageView) view.findViewById(R.id.colorImageView);
            percent_TV = (TextView) view.findViewById(R.id.percent_TV);
            title_TV = (TextView) view.findViewById(R.id.titleTextView);
            amount_TV = (TextView) view.findViewById(R.id.amountTextView);
            view.setTag(this);
        }
    }
}
