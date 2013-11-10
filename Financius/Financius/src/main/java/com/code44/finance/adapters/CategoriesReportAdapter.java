package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.model.CategoriesPeriodReport;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;

/**
 * Created by Mantas on 08/06/13.
 */
public class CategoriesReportAdapter extends BaseAdapter
{
    private final Context context;
    private final int primaryColor;
    private final int secondaryColor;
    private CategoriesPeriodReport report;

    public CategoriesReportAdapter(Context context)
    {
        this.context = context;
        this.primaryColor = context.getResources().getColor(R.color.text_primary);
        this.secondaryColor = context.getResources().getColor(R.color.text_secondary);
        report = null;
    }

    @Override
    public int getCount()
    {
        return report == null ? 0 : report.getTotalExpenseItemsCount();
    }

    @Override
    public Object getItem(int position)
    {
        return report.getExpenseItem(position);
    }

    @Override
    public long getItemId(int position)
    {
        return ((CategoriesPeriodReport.CategoriesPeriodReportItem) getItem(position)).getId();
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.li_category_report, parent, false);
            holder = new ViewHolder();
            holder.percentContainer_V = convertView.findViewById(R.id.percentContainer_V);
            holder.percent_TV = (TextView) convertView.findViewById(R.id.percent_TV);
            holder.title_TV = (TextView) convertView.findViewById(R.id.title_TV);
            holder.amount_TV = (TextView) convertView.findViewById(R.id.amount_TV);
            holder.separator_V = convertView.findViewById(R.id.separator_V);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        final CategoriesPeriodReport.CategoriesPeriodReportItem item = (CategoriesPeriodReport.CategoriesPeriodReportItem) getItem(position);
        if (item.getLevel() == 2)
        {
            // Sub-category
            holder.percentContainer_V.setVisibility(View.INVISIBLE);
            holder.title_TV.setTextColor(secondaryColor);
            holder.amount_TV.setTextColor(secondaryColor);
        }
        else
        {
            // Main category
            holder.percentContainer_V.setVisibility(View.VISIBLE);
            holder.percentContainer_V.setBackgroundColor(item.getColor());
            holder.percent_TV.setText(String.format("%.0f", item.getPieChartFraction() * 100));
            holder.title_TV.setTextColor(primaryColor);
            holder.amount_TV.setTextColor(primaryColor);
        }

        holder.separator_V.setVisibility(position == getCount() - 1 || ((CategoriesPeriodReport.CategoriesPeriodReportItem) getItem(position + 1)).getLevel() != 2 ? View.VISIBLE : View.INVISIBLE);
        holder.title_TV.setText(item.getTitle());
        holder.amount_TV.setText(AmountUtils.formatAmount(context, CurrenciesHelper.getDefault(context).getMainCurrencyId(), item.getAmount()));

        return convertView;
    }

    public void setReport(CategoriesPeriodReport report)
    {
        this.report = report;
        notifyDataSetChanged();
    }

    private static class ViewHolder
    {
        public View percentContainer_V;
        public TextView percent_TV;
        public TextView title_TV;
        public TextView amount_TV;
        public View separator_V;
    }
}
