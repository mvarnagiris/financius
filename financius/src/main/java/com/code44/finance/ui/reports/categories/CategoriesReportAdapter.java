package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.money.AmountFormatter;

import java.util.List;

public class CategoriesReportAdapter extends BaseAdapter {
    private final Context context;
    private final AmountFormatter amountFormatter;
    private CategoriesReportData categoriesReportData;
    private long totalAmount = 0;

    public CategoriesReportAdapter(Context context, AmountFormatter amountFormatter) {
        this.context = context;
        this.amountFormatter = amountFormatter;
    }

    @Override public int getCount() {
        return categoriesReportData != null ? categoriesReportData.size() : 0;
    }

    @Override public Object getItem(int position) {
        return categoriesReportData.get(position);
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

        final CategoriesReportData.CategoriesReportItem item = categoriesReportData.get(position);
        holder.color_IV.setColorFilter(item.getCategory().getColor());
        final int percent = getPercent(item.getAmount());
        holder.percent_TV.setText((percent == 0 ? "<1" : (percent == 100 && getCount() > 1 ? ">99" : percent)) + "%");
        holder.title_TV.setText(item.getCategory().getTitle());
        holder.amount_TV.setText(amountFormatter.format(item.getAmount()));
        bindTags((ViewGroup) convertView, item.getTags());

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

    private void bindTags(ViewGroup parent, List<Pair<Tag, Long>> tags) {
        final int staticViewCount = 1;
        final int currentCount = parent.getChildCount() - staticViewCount;
        final int newCount = tags.size();
        if (newCount > currentCount) {
            for (int i = currentCount; i < newCount; i++) {
                final View view = LayoutInflater.from(context).inflate(R.layout.li_category_report_tag, parent, false);
                new TagViewHolder(view);
                parent.addView(view);
            }
        } else {
            parent.removeViews(staticViewCount, currentCount - newCount);
        }

        for (int i = staticViewCount, size = staticViewCount + tags.size(); i < size; i++) {
            final TagViewHolder holder = (TagViewHolder) parent.getChildAt(i).getTag();
            final Pair<Tag, Long> tagAmount = tags.get(i - staticViewCount);
            holder.title_TV.setText(tagAmount.first.getTitle());
            holder.amount_TV.setText(amountFormatter.format(tagAmount.second));
        }
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

    private static final class TagViewHolder {
        public final TextView title_TV;
        public final TextView amount_TV;

        public TagViewHolder(View view) {
            title_TV = (TextView) view.findViewById(R.id.titleTextView);
            amount_TV = (TextView) view.findViewById(R.id.amountTextView);
            view.setTag(this);
        }
    }
}
