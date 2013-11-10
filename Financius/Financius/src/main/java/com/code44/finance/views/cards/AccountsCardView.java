package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.model.Account;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CardViewUtils;
import com.code44.finance.utils.CurrenciesHelper;
import com.code44.finance.views.AccountView;

import java.util.List;

@SuppressWarnings({"UnusedDeclaration", "ConstantConditions"})
public class AccountsCardView extends TitleCardView
{
    public static final long UNIQUE_CARD_ID = CardViewUtils.ID_ACCOUNTS;
    // -----------------------------------------------------------------------------------------------------------------
    private final LinearLayout container_V;
    private final View separator_V;
    private final LinearLayout balanceContainer_V;
    private final TextView balance_TV;


    public AccountsCardView(Context context)
    {
        this(context, null);
    }

    public AccountsCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public AccountsCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Init
        container_V = new LinearLayout(context);
        separator_V = new View(context);
        balanceContainer_V = new LinearLayout(context);
        balance_TV = new TextView(context);
        final TextView balanceTitle_TV = new TextView(context);

        // Setup
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        container_V.setLayoutParams(params);
        container_V.setOrientation(LinearLayout.VERTICAL);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.separator_thin));
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.space_normal);
        separator_V.setLayoutParams(params);
        separator_V.setBackgroundColor(getResources().getColor(R.color.separator));
        separator_V.setVisibility(GONE);

        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        balanceContainer_V.setLayoutParams(params);
        balanceContainer_V.setOrientation(LinearLayout.HORIZONTAL);

        balanceTitle_TV.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        balanceTitle_TV.setText(R.string.balance);
        balanceTitle_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
        balanceTitle_TV.setTextColor(getResources().getColor(R.color.text_secondary));

        balance_TV.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        balance_TV.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
        balance_TV.setTextColor(getResources().getColor(R.color.text_secondary));

        // Add views
        balanceContainer_V.addView(balanceTitle_TV);
        balanceContainer_V.addView(balance_TV);
        addView(container_V);
        addView(separator_V);
        addView(balanceContainer_V);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int extraHeight = 0;

        // Container
        LayoutParams params = (LayoutParams) container_V.getLayoutParams();
        final int containerWidthMS = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY);
        final int containerHeightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        container_V.measure(containerWidthMS, containerHeightMS);
        extraHeight += container_V.getMeasuredHeight() + params.topMargin + params.bottomMargin;

        // Separator
        if (separator_V.getVisibility() != GONE)
        {
            params = (LayoutParams) separator_V.getLayoutParams();
            final int widthMS = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY);
            final int heightMS = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
            separator_V.measure(widthMS, heightMS);
            extraHeight += separator_V.getMeasuredHeight() + params.topMargin + params.bottomMargin;
        }

        // Balance container
        if (balanceContainer_V.getVisibility() != GONE)
        {
            params = (LayoutParams) balanceContainer_V.getLayoutParams();
            final int widthMS = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight() - params.leftMargin - params.rightMargin, MeasureSpec.EXACTLY);
            final int heightMS = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            balanceContainer_V.measure(widthMS, heightMS);
            extraHeight += balanceContainer_V.getMeasuredHeight() + params.topMargin + params.bottomMargin;
        }

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + extraHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);

        final int parentLeft = getPaddingLeft();
        final int parentBottom = bottom - top - getPaddingBottom();

        int childTop = getContentTop();

        // Container
        LayoutParams params = (LayoutParams) container_V.getLayoutParams();
        childTop += params.topMargin;
        int childLeft = parentLeft + params.leftMargin;
        container_V.layout(childLeft, childTop, childLeft + container_V.getMeasuredWidth(), childTop + container_V.getMeasuredHeight());
        childTop = container_V.getBottom() + params.bottomMargin;

        // Separator
        if (separator_V.getVisibility() != GONE)
        {
            params = (LayoutParams) separator_V.getLayoutParams();
            childTop += params.topMargin;
            childLeft = parentLeft + params.leftMargin;
            separator_V.layout(childLeft, childTop, childLeft + separator_V.getMeasuredWidth(), childTop + separator_V.getMeasuredHeight());
            childTop = separator_V.getBottom() + params.bottomMargin;
        }

        // Balance container
        if (balanceContainer_V.getVisibility() != GONE)
        {
            params = (LayoutParams) balanceContainer_V.getLayoutParams();
            childTop += params.topMargin;
            childLeft = parentLeft + params.leftMargin;
            balanceContainer_V.layout(childLeft, childTop, childLeft + balanceContainer_V.getMeasuredWidth(), childTop + balanceContainer_V.getMeasuredHeight());
        }
    }

    @Override
    public void setCardInfo(CardInfo cardInfo)
    {
        super.setCardInfo(cardInfo);
        setAccountList(((AccountsCardInfo) cardInfo).getAccountList());
    }

    public void setAccountList(List<Account> accountList)
    {
        final int currentSize = container_V.getChildCount();
        final int newSize = accountList != null ? accountList.size() : 0;

        // Add/Remove views
        if (newSize > currentSize)
        {
            // Add missing views
            View view;
            for (int i = currentSize; i < newSize; i++)
            {
                view = new AccountView(getContext());
                view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                container_V.addView(view);
            }
        }
        else if (newSize < currentSize)
        {
            // Remove unnecessary views
            final int removeSize = Math.max(currentSize - newSize, 0);
            container_V.removeViews(0, removeSize);
        }

        // Update values
        Account account;
        AccountView view;
        double balance;
        double totalBalance = 0;
        for (int i = 0; i < newSize; i++)
        {
            account = accountList.get(i);
            view = (AccountView) container_V.getChildAt(i);

            balance = account.getBalance();
            view.bind(account.getTitle(), balance, account.getCurrency().getId());
            totalBalance += balance * account.getCurrency().getExchangeRate();
        }

        // Update total balance
        if (newSize >= 2)
        {
            separator_V.setVisibility(View.VISIBLE);
            balanceContainer_V.setVisibility(View.VISIBLE);
            balance_TV.setText(AmountUtils.formatAmount(getContext(), CurrenciesHelper.getDefault(getContext()).getMainCurrencyId(), totalBalance));
            balance_TV.setTextColor(AmountUtils.getBalanceColor(getContext(), totalBalance, false));
        }
        else
        {
            separator_V.setVisibility(View.GONE);
            balanceContainer_V.setVisibility(View.GONE);
        }
    }

    public static class AccountsCardInfo extends TitleCardInfo
    {
        private List<Account> accountList;

        public AccountsCardInfo(Context context)
        {
            super(UNIQUE_CARD_ID);
            setTitle(context.getString(R.string.accounts));
        }

        public List<Account> getAccountList()
        {
            return accountList;
        }

        public AccountsCardInfo setAccountList(List<Account> accountList)
        {
            this.accountList = accountList;
            return this;
        }
    }
}
