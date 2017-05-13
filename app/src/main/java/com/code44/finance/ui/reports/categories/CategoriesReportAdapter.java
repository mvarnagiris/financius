package com.code44.finance.ui.reports.categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.TransactionType;
import com.code44.finance.money.AmountFormatter;
import com.code44.finance.money.grouping.CategoryTagGroup;
import com.code44.finance.money.grouping.TagGroup;
import com.code44.finance.utils.CategoryUtils;

import java.util.Collection;
import java.util.List;

public class CategoriesReportAdapter extends BaseAdapter {
    private final Context context;
    private final AmountFormatter amountFormatter;
    private List<CategoryTagGroup> categoryGroups;
    private long totalAmount = 0;

    public CategoriesReportAdapter(Context context, AmountFormatter amountFormatter) {
        this.context = context;
        this.amountFormatter = amountFormatter;
    }

    @Override public int getCount() {
        return categoryGroups != null ? categoryGroups.size() : 0;
    }

    @Override public Object getItem(int position) {
        return categoryGroups.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public boolean isEnabled(int position) {
        return false;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.li_category_report, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CategoryTagGroup item = categoryGroups.get(position);
        // TODO Transaction type should come from Activity
        holder.colorImageView.setColorFilter(CategoryUtils.getColor(context, item.getCategory(), TransactionType.Expense));
        final int percent = getPercent(item.getValue());
        holder.percentTextView.setText((percent == 0 ? "<1" : (percent == 100 && getCount() > 1 ? ">99" : percent)) + "%");
        holder.titleTextView.setText(CategoryUtils.getTitle(context, item.getCategory(), TransactionType.Expense));
        holder.amountTextView.setText(amountFormatter.format(item.getValue()));
        bindTags((ViewGroup) convertView, item.getTagGroups());

        return convertView;
    }

    public void setData(List<CategoryTagGroup> groups) {
        totalAmount = 0;
        for (CategoryTagGroup group : groups) {
            totalAmount += group.getValue();
        }
        this.categoryGroups = groups;
        notifyDataSetChanged();
    }

    private int getPercent(long amount) {
        return Math.round(100.0f * amount / totalAmount);
    }

    private void bindTags(ViewGroup parent, Collection<TagGroup> tagGroups) {
        final int staticViewCount = 1;
        final int currentCount = parent.getChildCount() - staticViewCount;
        final int newCount = tagGroups.size();
        if (newCount > currentCount) {
            for (int i = currentCount; i < newCount; i++) {
                final View view = LayoutInflater.from(context).inflate(R.layout.li_category_report_tag, parent, false);
                new TagViewHolder(view);
                parent.addView(view);
            }
        } else {
            parent.removeViews(staticViewCount, currentCount - newCount);
        }

        int i = staticViewCount;
        for (TagGroup tagGroup : tagGroups) {
            final TagViewHolder holder = (TagViewHolder) parent.getChildAt(i).getTag();
            holder.titleTextView.setText(tagGroup.getTag().getTitle());
            holder.amountTextView.setText(amountFormatter.format(tagGroup.getValue()));
            i++;
        }
    }

    private static final class ViewHolder {
        public final ImageView colorImageView;
        public final TextView percentTextView;
        public final TextView titleTextView;
        public final TextView amountTextView;

        public ViewHolder(View view) {
            colorImageView = (ImageView) view.findViewById(R.id.colorImageView);
            percentTextView = (TextView) view.findViewById(R.id.percentTextView);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            amountTextView = (TextView) view.findViewById(R.id.amountTextView);
            view.setTag(this);
        }
    }

    private static final class TagViewHolder {
        public final TextView titleTextView;
        public final TextView amountTextView;

        public TagViewHolder(View view) {
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            amountTextView = (TextView) view.findViewById(R.id.amountTextView);
            view.setTag(this);
        }
    }
}
