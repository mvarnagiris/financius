package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.views.BudgetCardView;
import com.code44.finance.views.reports.BudgetView;

@SuppressWarnings("ConstantConditions")
public class BudgetsAdapter extends AbstractCursorAdapter
{
    private static final int VT_HEADER = 0;
    private static final int VT_NORMAL = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private int iTitle;
    private int iSum;
    private int iAmount;
    private int iPeriodType;
    // -----------------------------------------------------------------------------------------------------------------
    private int periodType;
    private long periodStart;
    private long periodEnd;
    private double totalSum;
    private double totalAmount;

    public BudgetsAdapter(Context context)
    {
        super(context, null);
    }

    @Override
    public int getCount()
    {
        return super.getCount() + (mDataValid ? 1 : 0);
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position == 0 ? VT_HEADER : VT_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (position == 0)
        {
            if (convertView == null)
                convertView = new BudgetCardView(mContext);

            ((BudgetCardView) convertView).bind(periodType, periodStart, periodEnd, totalSum, totalAmount);
            return convertView;
        }
        return super.getView(position - 1, convertView, parent);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.li_budget, viewGroup, false);
        final ViewHolder holder = new ViewHolder();
        holder.title_TV = (TextView) view.findViewById(R.id.title_TV);
        holder.budget_V = (BudgetView) view.findViewById(R.id.budget_V);
        holder.sum_TV = (TextView) view.findViewById(R.id.sum_TV);
        holder.amount_TV = (TextView) view.findViewById(R.id.amount_TV);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        final ViewHolder holder = (ViewHolder) view.getTag();

        final double sum = cursor.getDouble(iSum);
        final double amount = cursor.getDouble(iAmount);

        holder.title_TV.setText(cursor.getString(iTitle));
        holder.budget_V.setProgress((float) (sum / amount));
        holder.sum_TV.setText(AmountUtils.formatAmount(context, CurrenciesHelper.getDefault(context).getMainCurrencyId(), sum));
        holder.amount_TV.setText(AmountUtils.formatAmount(context, CurrenciesHelper.getDefault(context).getMainCurrencyId(), amount));
    }

    @Override
    protected void findIndexes(Cursor c)
    {
        iTitle = c.getColumnIndex(Tables.Budgets.TITLE);
        iSum = c.getColumnIndex(Tables.Budgets.SUM);
        iAmount = c.getColumnIndex(Tables.Budgets.AMOUNT);
        iPeriodType = c.getColumnIndex(Tables.Budgets.PERIOD);
    }

    public void bindHeader(int periodType, long periodStart, long periodEnd, double totalSum, double totalAmount)
    {
        this.periodType = periodType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalSum = totalSum;
        this.totalAmount = totalAmount;
    }

    private static class ViewHolder
    {
        public TextView title_TV;
        public BudgetView budget_V;
        public TextView sum_TV;
        public TextView amount_TV;
    }
}
