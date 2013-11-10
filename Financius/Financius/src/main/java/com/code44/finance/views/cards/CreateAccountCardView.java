package com.code44.finance.views.cards;

import android.content.Context;
import android.util.AttributeSet;
import com.code44.finance.R;
import com.code44.finance.utils.CardViewUtils;

@SuppressWarnings("UnusedDeclaration")
public class CreateAccountCardView extends BigTextCardView
{
    public static final long UNIQUE_CARD_ID = CardViewUtils.ID_CREATE_ACCOUNT;

    public CreateAccountCardView(Context context)
    {
        this(context, null);
    }

    public CreateAccountCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CreateAccountCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        getTitleView().setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_new, 0, 0, 0);
        setCardInfo(new CreateAccountCardInfo(context));
    }

    public static class CreateAccountCardInfo extends BigTextCardInfo
    {
        public CreateAccountCardInfo(Context context)
        {
            super(UNIQUE_CARD_ID);
            setTitle(context.getString(R.string.create_account));
        }
    }
}
