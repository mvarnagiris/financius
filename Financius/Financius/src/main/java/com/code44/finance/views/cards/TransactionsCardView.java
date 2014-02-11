package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CardViewUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.views.reports.ExpenseGraphView;

import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class TransactionsCardView extends PeriodCardView
{
    public static final long UNIQUE_CARD_ID = CardViewUtils.ID_TRANSACTIONS;
    // -----------------------------------------------------------------------------------------------------------------
    private final ExpenseGraphView expenseGraph_V;
    private final LinearLayout valuesContainer_V;
    private final TextView expense_TV;
    private final TextView income_TV;
    private final TextView profit_TV;

    public TransactionsCardView(Context context)
    {
        this(context, null);
    }

    public TransactionsCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public TransactionsCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Init
        expenseGraph_V = new ExpenseGraphView(context);
        valuesContainer_V = new LinearLayout(context);
        expense_TV = new TextView(context);
        income_TV = new TextView(context);
        profit_TV = new TextView(context);

        // Setup
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.recommended_touch_size));
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        expenseGraph_V.setLayoutParams(params);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        valuesContainer_V.setLayoutParams(params);

        income_TV.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        income_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        income_TV.setTextColor(getResources().getColor(R.color.text_green));
        income_TV.setVisibility(GONE);

        expense_TV.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        expense_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        expense_TV.setTextColor(getResources().getColor(R.color.text_red));
        expense_TV.setGravity(Gravity.RIGHT);
        expense_TV.setVisibility(GONE);

        profit_TV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        profit_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_normal));
        profit_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        profit_TV.setVisibility(GONE);

        // Add views
        valuesContainer_V.addView(income_TV);
        valuesContainer_V.addView(profit_TV);
        valuesContainer_V.addView(expense_TV);
        addView(expenseGraph_V);
        addView(valuesContainer_V);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int extraHeight = 0;

        // Expense graph
        LayoutParams params = (LayoutParams) expenseGraph_V.getLayoutParams();
        int widthMS = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY);
        int heightMS = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
        expenseGraph_V.measure(widthMS, heightMS);
        extraHeight += expenseGraph_V.getMeasuredHeight() + params.topMargin + params.bottomMargin;

        // Values container
        params = (LayoutParams) valuesContainer_V.getLayoutParams();
        widthMS = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY);
        heightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        valuesContainer_V.measure(widthMS, heightMS);
        extraHeight += valuesContainer_V.getMeasuredHeight() + params.topMargin + params.bottomMargin;

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + extraHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        final int parentLeft = getPaddingLeft();
        final int parentBottom = bottom - top - getPaddingBottom();

        int childTop = getContentTop();

        // Expense graph
        LayoutParams params = (LayoutParams) expenseGraph_V.getLayoutParams();
        childTop += params.topMargin;
        int childLeft = parentLeft + params.leftMargin;
        expenseGraph_V.layout(childLeft, childTop, childLeft + expenseGraph_V.getMeasuredWidth(), childTop + expenseGraph_V.getMeasuredHeight());
        childTop = expenseGraph_V.getBottom() + params.bottomMargin;

        // Values container
        params = (LayoutParams) valuesContainer_V.getLayoutParams();
        childTop += params.topMargin;
        childLeft = parentLeft + params.leftMargin;
        valuesContainer_V.layout(childLeft, childTop, childLeft + valuesContainer_V.getMeasuredWidth(), childTop + valuesContainer_V.getMeasuredHeight());
    }

    @Override
    public void setCardInfo(CardInfo cardInfo)
    {
        super.setCardInfo(cardInfo);

        // Find current index in period
        final TransactionsCardInfo info = (TransactionsCardInfo) cardInfo;
        int currentIndex;
        if (info.getPeriodType() == PeriodHelper.TYPE_YEAR)
            currentIndex = PeriodHelper.getMonthCountInPeriod(info.getPeriodStart(), System.currentTimeMillis());
        else if (info.getPeriodType() == PeriodHelper.TYPE_DAY)
            currentIndex = PeriodHelper.getHourCountInPeriod(info.getPeriodStart(), System.currentTimeMillis());
        else
            currentIndex = PeriodHelper.getDayCountInPeriod(info.getPeriodStart(), System.currentTimeMillis());
        currentIndex--;

        expenseGraph_V.bind(info.getItemList(), currentIndex);

        final double income = info.getIncome();
        final double expense = info.getExpense();

        final long mainCurrencyId = CurrenciesHelper.getDefault(getContext()).getMainCurrencyId();
        if (income > 0)
        {
            income_TV.setVisibility(VISIBLE);
            income_TV.setText(AmountUtils.formatAmount(getContext(), mainCurrencyId, income));
        }
        else
        {
            income_TV.setVisibility(GONE);
        }

        if (expense > 0)
        {
            expense_TV.setVisibility(VISIBLE);
            expense_TV.setText(AmountUtils.formatAmount(mainCurrencyId, expense));
        }
        else
        {
            expense_TV.setVisibility(GONE);
        }

        if (expense > 0 && income > 0)
        {
            final double profit = income - expense;
            profit_TV.setVisibility(VISIBLE);
            profit_TV.setText(AmountUtils.formatAmount(getContext(), mainCurrencyId, profit));
            profit_TV.setTextColor(AmountUtils.getBalanceColor(getContext(), profit, false));
        }
        else
        {
            profit_TV.setVisibility(GONE);
        }
    }

    public static class TransactionsCardInfo extends PeriodCardInfo
    {
        private double income;
        private double expense;
        private List<Float> itemList;

        public TransactionsCardInfo(Context context)
        {
            super(context, UNIQUE_CARD_ID);

            setTitle(context.getString(R.string.transactions));
            final PeriodHelper periodHelper = PeriodHelper.getDefault(context);
            setPeriod(periodHelper.getType(), periodHelper.getCurrentStart(), periodHelper.getCurrentEnd());
        }

        public List<Float> getItemList()
        {
            return itemList;
        }

        public TransactionsCardInfo setItemList(List<Float> itemList)
        {
            this.itemList = itemList;
            return this;
        }

        public double getIncome()
        {
            return income;
        }

        public TransactionsCardInfo setIncome(double income)
        {
            this.income = income;
            return this;
        }

        public double getExpense()
        {
            return expense;
        }

        public TransactionsCardInfo setExpense(double expense)
        {
            this.expense = expense;
            return this;
        }
    }
}