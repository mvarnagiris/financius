package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.utils.AmountUtils;
import com.code44.finance.views.AutoResizeTextView;

@SuppressWarnings("UnusedDeclaration")
public class AmountCardView extends CardViewV2 implements View.OnClickListener
{
    private final AutoResizeTextView amount_TV;
    private final AutoResizeTextView exchangeRate_TV;
    private final AutoResizeTextView amountTo_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private Callback callback;
    private double amount = 0;
    private double exchangeRate = 1;
    private long currencyId = 0;

    public AmountCardView(Context context)
    {
        this(context, null);
    }

    public AmountCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public AmountCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup parent
        //noinspection ConstantConditions
        final int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        //noinspection ConstantConditions
        container_V.setPadding(0, 0, 0, 0);
        content_FL.setPadding(0, 0, padding, 0);
        final MarginLayoutParams mlp = (MarginLayoutParams) listContainerSeparator_V.getLayoutParams();
        //noinspection ConstantConditions
        mlp.leftMargin = padding;
        mlp.topMargin = 0;
        mlp.bottomMargin = 0;

        // Setup amount
        amount_TV = new AutoResizeTextView(context);
        amount_TV.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        amount_TV.setMaxLines(1);
        //noinspection ConstantConditions
        amount_TV.setTextColor(getResources().getColor(R.color.text_primary));
        amount_TV.setTextSize(getResources().getDimension(R.dimen.text_xxxlarge));
        amount_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        amount_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.big_touch_size));
        setContentView(amount_TV);

        // Setup exchange rate
        exchangeRate_TV = new AutoResizeTextView(context);
        exchangeRate_TV.setPadding(padding, 0 ,padding, 0);
        exchangeRate_TV.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        exchangeRate_TV.setMaxLines(1);
        //noinspection ConstantConditions
        exchangeRate_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        exchangeRate_TV.setTextSize(getResources().getDimension(R.dimen.text_xxlarge));
        exchangeRate_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        exchangeRate_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size));
        exchangeRate_TV.setBackgroundResource(R.drawable.card_selector);
        exchangeRate_TV.setOnClickListener(this);
        listContainer_LL.addView(exchangeRate_TV);

        // Setup exchange rate
        amountTo_TV = new AutoResizeTextView(context);
        amountTo_TV.setPadding(padding, 0 ,padding, 0);
        amountTo_TV.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        amountTo_TV.setMaxLines(1);
        //noinspection ConstantConditions
        amountTo_TV.setTextColor(getResources().getColor(R.color.text_secondary));
        amountTo_TV.setTextSize(getResources().getDimension(R.dimen.text_xxlarge));
        amountTo_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        amountTo_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size));
        amountTo_TV.setBackgroundResource(R.drawable.card_selector);
        amountTo_TV.setOnClickListener(this);
        listContainer_LL.addView(amountTo_TV);

        // Setup icon
        //noinspection ConstantConditions
        ((MarginLayoutParams) icon_IV.getLayoutParams()).leftMargin = -container_V.getPaddingLeft();
        icon_IV.setPadding(container_V.getPaddingLeft(), 0, 0, 0);
        icon_IV.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.big_touch_size));
        icon_IV.setBackgroundResource(R.drawable.card_selector);
        icon_IV.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (callback != null)
                    callback.onChangeCategoryType();
            }
        });

        if (isInEditMode())
            setAmount(275.19, 0, Tables.Categories.Type.EXPENSE);
        else
            setAmount(0.0, 0, Tables.Categories.Type.EXPENSE);
    }

    @Override
    public void onClick(View v)
    {

    }

    public void setAmount(double amount, long currencyId, int categoryType)
    {
        this.amount = amount;
        amount_TV.setText(AmountUtils.formatAmount(getContext(), currencyId, amount));
        //noinspection ConstantConditions
        amount_TV.setTextColor(getResources().getColor(categoryType == Tables.Categories.Type.EXPENSE ? R.color.text_red : categoryType == Tables.Categories.Type.INCOME ? R.color.text_green : R.color.text_yellow));
        setIcon(getResources().getDrawable(categoryType == Tables.Categories.Type.EXPENSE ? R.drawable.ic_category_type_expense : categoryType == Tables.Categories.Type.INCOME ? R.drawable.ic_category_type_income : R.drawable.ic_category_type_transfer));
        setExchangeRate(exchangeRate, currencyId);
    }

    public double getAmount()
    {
        return amount;
    }

    public void setExchangeRate(double exchangeRate, long currencyId)
    {
        this.exchangeRate = exchangeRate;
        this.currencyId = currencyId;
        exchangeRate_TV.setText(String.valueOf(exchangeRate));
        amountTo_TV.setText(AmountUtils.formatAmount(getContext(), currencyId, amount * exchangeRate));
    }

    public double getExchangeRate()
    {
        return exchangeRate;
    }

    public void setExchangeRateVisible(boolean visible)
    {
        setListVisible(visible);
    }

    public void setCallback(Callback callback)
    {
        this.callback = callback;
    }

    public static interface Callback
    {
        public void onChangeCategoryType();
    }
}
