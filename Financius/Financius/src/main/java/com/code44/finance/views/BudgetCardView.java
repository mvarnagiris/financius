package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.reports.BudgetView;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class BudgetCardView extends LinearLayout
{
    private final TextView period_TV;
    private final TextView sum_TV;
    private final TextView amount_TV;
    private final BudgetView budget_V;

    public BudgetCardView(Context context)
    {
        this(context, null);
    }

    public BudgetCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BudgetCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflate(context, R.layout.v_budget_card, this);

        // Setup layout
        setOrientation(VERTICAL);

        // Setup card
        setBackgroundResource(R.drawable.bg_card_old);
        final int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        setPadding(getPaddingLeft() + padding, getPaddingTop() + padding, getPaddingRight() + padding, getPaddingBottom() + padding);

        // Get views
        period_TV = (TextView) findViewById(R.id.period_TV);
        sum_TV = (TextView) findViewById(R.id.sum_TV);
        amount_TV = (TextView) findViewById(R.id.amount_TV);
        budget_V = (BudgetView) findViewById(R.id.budget_V);
    }

    public void bind(int periodType, long periodStart, long periodEnd, double sum, double amount)
    {
        period_TV.setText(PeriodHelper.getPeriodShortTitle(getContext(), periodType, periodStart, periodEnd));
        final long mainCurrencyId = CurrenciesHelper.getDefault(getContext()).getMainCurrencyId();
        sum_TV.setText(AmountUtils.formatAmount(getContext(), mainCurrencyId, sum));
        amount_TV.setText(AmountUtils.formatAmount(getContext(), mainCurrencyId, amount));
        budget_V.setProgress((float) (sum / amount));
    }
}
