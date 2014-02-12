package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.model.Account;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.utils.CurrencyHelper;

import java.util.List;

public class AccountsCardView extends LinearLayout
{
    private final TextView balance_TV;
    private final View separator_V;
    private final View balanceContainer_V;
    private final TextView createAccount_TV;
    private final LinearLayout container_V;
    private boolean hasAccounts = false;

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
        inflate(context, R.layout.v_accounts_card, this);

        // Setup layout
        setOrientation(VERTICAL);

        // Setup card
        setBackgroundResource(R.drawable.bg_card_old);
        final int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        setPadding(getPaddingLeft() + padding, getPaddingTop() + padding, getPaddingRight() + padding, getPaddingBottom() + padding);

        // Get views
        balance_TV = (TextView) findViewById(R.id.balance_TV);
        separator_V = findViewById(R.id.separator_V);
        balanceContainer_V = findViewById(R.id.balanceContainer_V);
        createAccount_TV = (TextView) findViewById(R.id.createAccount_TV);
        container_V = (LinearLayout) findViewById(R.id.container_V);
    }

    public void bind(List<Account> accountsList)
    {
        final int currentSize = container_V.getChildCount();
        final int newSize = accountsList != null ? accountsList.size() : 0;

        // Add/Remove views
        if (newSize > currentSize)
        {
            // Add missing views
            View view;
            for (int i = currentSize; i < newSize; i++)
            {
                view = new AccountView(getContext());
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
        double balance = 0;
        double totalBalance = 0;
        for (int i = 0; i < newSize; i++)
        {
            account = accountsList.get(i);
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
            createAccount_TV.setVisibility(View.GONE);
            balance_TV.setText(AmountUtils.formatAmount(CurrencyHelper.get().getMainCurrencyId(), totalBalance));
            balance_TV.setTextColor(AmountUtils.getBalanceColor(getContext(), totalBalance, false));
        }
        else
        {
            separator_V.setVisibility(View.GONE);
            balanceContainer_V.setVisibility(View.GONE);
            createAccount_TV.setVisibility(newSize == 0 ? View.VISIBLE : View.GONE);
        }

        hasAccounts = newSize > 0;
    }

    public boolean hasAccounts()
    {
        return hasAccounts;
    }
}
