package com.code44.finance.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Currency;
import com.code44.finance.utils.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesReportAdapter extends BaseAdapter {
    private final Context context;
    private final Currency defaultCurrency;
    private final List<Pair<Category, Long>> items;
    private long totalAmount = 0;

    public CategoriesReportAdapter(Context context, Currency defaultCurrency) {
        this.context = context;
        this.defaultCurrency = defaultCurrency;
        this.items = new ArrayList<>();
    }

    @Override public int getCount() {
        return items.size();
    }

    @Override public Object getItem(int position) {
        return items.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.li_category_report, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Pair<Category, Long> categoryAmountPair = items.get(position);
        holder.color_IV.setColorFilter(categoryAmountPair.first.getColor());
        holder.percent_TV.setText(getPercent(categoryAmountPair.second) + "%");
        holder.title_TV.setText(categoryAmountPair.first.getTitle());
        holder.amount_TV.setText(MoneyFormatter.format(defaultCurrency, categoryAmountPair.second));

        return convertView;
    }

    public void setItems(List<Pair<Category, Long>> items, long totalAmount) {
        this.items.clear();
        if (items != null) {
            this.items.addAll(items);
        }
        this.totalAmount = totalAmount;
        notifyDataSetChanged();
    }

    private int getPercent(long amount) {
        return Math.round(100.0f * amount / totalAmount);
    }

    private static final class ViewHolder {
        public ImageView color_IV;
        public TextView percent_TV;
        public TextView title_TV;
        public TextView amount_TV;

        public ViewHolder(View view) {
            color_IV = (ImageView) view.findViewById(R.id.color_IV);
            percent_TV = (TextView) view.findViewById(R.id.percent_TV);
            title_TV = (TextView) view.findViewById(R.id.title_TV);
            amount_TV = (TextView) view.findViewById(R.id.amount_TV);
            view.setTag(this);
        }
    }
}
