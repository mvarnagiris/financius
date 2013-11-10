package com.code44.finance.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.utils.AmountUtils;

/**
 * Created by Mantas on 25/05/13.
 */
public class AccountView extends LinearLayout
{
    private final TextView title_TV;
    private final TextView balance_TV;

    public AccountView(Context context)
    {
        this(context, null);
    }

    public AccountView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public AccountView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        inflate(context, R.layout.v_account, this);

        // Get views
        title_TV = (TextView) findViewById(R.id.title_TV);
        balance_TV = (TextView) findViewById(R.id.balance_TV);
    }

    public void bind(String title, double balance, long currencyId)
    {
        title_TV.setText(title);
        balance_TV.setText(AmountUtils.formatAmount(getContext(), currencyId, balance));
        balance_TV.setTextColor(AmountUtils.getBalanceColor(getContext(), balance));
    }
}
