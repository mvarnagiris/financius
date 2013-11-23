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
public class AmountCardView extends CardViewV2
{
    private final AutoResizeTextView amount_TV;
    // -----------------------------------------------------------------------------------------------------------------
    private Callback callback;
    private double amount;

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

        // Setup
        //noinspection ConstantConditions
        container_V.setPadding(container_V.getPaddingLeft(), 0, getResources().getDimensionPixelSize(R.dimen.space_normal), 0);
        amount_TV = new AutoResizeTextView(context);
        amount_TV.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        amount_TV.setMaxLines(1);
        //noinspection ConstantConditions
        amount_TV.setTextColor(getResources().getColor(R.color.text_primary));
        amount_TV.setTextSize(getResources().getDimension(R.dimen.text_xxxlarge));
        amount_TV.setMinTextSize(getResources().getDimension(R.dimen.text_xsmall));
        amount_TV.setMinHeight(getResources().getDimensionPixelSize(R.dimen.big_touch_size));
        setContentView(amount_TV);
        //noinspection ConstantConditions
        ((MarginLayoutParams)icon_IV.getLayoutParams()).leftMargin = -container_V.getPaddingLeft();
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

    public void setAmount(double amount, long currencyId, int categoryType)
    {
        this.amount = amount;
        amount_TV.setText(AmountUtils.formatAmount(getContext(), currencyId, amount));
        //noinspection ConstantConditions
        amount_TV.setTextColor(getResources().getColor(categoryType == Tables.Categories.Type.EXPENSE ? R.color.text_red : categoryType == Tables.Categories.Type.INCOME ? R.color.text_green : R.color.text_yellow));
        setIcon(getResources().getDrawable(categoryType == Tables.Categories.Type.EXPENSE ? R.drawable.ic_category_type_expense : categoryType == Tables.Categories.Type.INCOME ? R.drawable.ic_category_type_income : R.drawable.ic_category_type_transfer));
    }

    public double getAmount()
    {
        return amount;
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
