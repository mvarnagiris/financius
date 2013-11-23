package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import com.code44.finance.R;
import com.code44.finance.views.AutoResizeTextView;

@SuppressWarnings("UnusedDeclaration")
public class AccountCardView extends CardViewV2
{
    private final AutoResizeTextView account_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private long accountId;
    private String accountTitle;
    private long currencyId;
    private String currencyCode;
    private double currencyExchangeRate;

    public AccountCardView(Context context)
    {
        this(context, null);
    }

    public AccountCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public AccountCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        //noinspection ConstantConditions
        int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        container_V.setPadding(padding, container_V.getPaddingTop(), padding, container_V.getPaddingBottom());
        account_TV = new AutoResizeTextView(context);
        account_TV.setGravity(Gravity.CENTER_VERTICAL);
        account_TV.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        //noinspection ConstantConditions
        account_TV.setTextColor(getResources().getColor(R.color.text_primary));
        account_TV.setTextSize(getResources().getDimension(R.dimen.text_xxxlarge));
        account_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        account_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size) - container_V.getPaddingTop() - container_V.getPaddingBottom());
        setContentView(account_TV);

        if (isInEditMode())
            setAccount(1, "Cash", 0, null, 0, getResources().getString(R.string.from_account));
        else
            setAccount(0, null, 0, null, 0, getResources().getString(R.string.from_account));
    }

    public void setAccount(long accountId, String accountTitle, long currencyId, String currencyCode, double currencyExchangeRate, String accountEmptyTitle)
    {
        this.accountId = accountId;
        this.accountTitle = accountTitle;
        this.currencyId = currencyId;
        this.currencyCode = currencyCode;
        this.currencyExchangeRate = currencyExchangeRate;

        if (accountId == 0)
        {
            account_TV.setText(accountEmptyTitle);
            //noinspection ConstantConditions
            account_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        }
        else
        {
            account_TV.setText(accountTitle);
            //noinspection ConstantConditions
            account_TV.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    public long getAccountId()
    {
        return accountId;
    }

    public String getAccountTitle()
    {
        return accountTitle;
    }

    public long getCurrencyId()
    {
        return currencyId;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }

    public double getCurrencyExchangeRate()
    {
        return currencyExchangeRate;
    }
}
