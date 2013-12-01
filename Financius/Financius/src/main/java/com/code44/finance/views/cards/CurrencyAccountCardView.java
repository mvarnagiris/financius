package com.code44.finance.views.cards;

import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.code44.finance.API;
import com.code44.finance.R;
import com.code44.finance.db.Tables;

@SuppressWarnings("UnusedDeclaration")
public class CurrencyAccountCardView extends CardViewV3
{
    private TextView title_TV;
    private Button currency_B;

    public CurrencyAccountCardView(Context context)
    {
        this(context, null);
    }

    public CurrencyAccountCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CurrencyAccountCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        setContent(R.layout.v_currency_account);
    }

    @Override
    public void setContent(View view)
    {
        super.setContent(view);

        //noinspection ConstantConditions
        view.setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size) - getPaddingTop() - getPaddingBottom());

        // Get views
        title_TV = (TextView) view.findViewById(R.id.title_TV);
        currency_B = (Button) view.findViewById(R.id.currency_B);
    }

    public void setData(final long currencyId, String currencyCode, final long accountId, String accountName, long accountCurrencyId, String accountCurrencyCode)
    {
        if (currencyId == accountCurrencyId)
        {
            //noinspection ConstantConditions
            title_TV.setTextColor(getResources().getColor(R.color.text_primary));
            title_TV.setText(accountName);
            currency_B.setBackgroundColor(0);
            currency_B.setTextColor(getResources().getColor(R.color.text_primary));
            currency_B.setEnabled(false);
            currency_B.setText(currencyCode);
            currency_B.setOnClickListener(null);
        }
        else
        {
            //noinspection ConstantConditions
            title_TV.setTextColor(getResources().getColor(R.color.text_secondary));
            title_TV.setText("(" + accountCurrencyCode + ") " + accountName);
            currency_B.setBackgroundResource(R.drawable.btn_primary);
            currency_B.setTextColor(getResources().getColor(R.color.text_primary_inverted));
            currency_B.setEnabled(true);
            currency_B.setText(currencyCode);
            currency_B.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final ContentValues values = new ContentValues();
                    values.put(Tables.Accounts.CURRENCY_ID, currencyId);
                    API.updateAccount(getContext(), accountId, values);
                }
            });
        }
    }
}
