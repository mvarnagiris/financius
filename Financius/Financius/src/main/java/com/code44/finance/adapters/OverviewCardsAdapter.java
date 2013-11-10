package com.code44.finance.adapters;

import android.content.Context;
import android.view.ViewGroup;
import com.code44.finance.views.cards.*;

public class OverviewCardsAdapter extends AbstractCardsAdapter
{
    private static final int POSITION_CREATE_ACCOUNT = 0;
    private static final int POSITION_ACCOUNTS = 1;
    private static final int POSITION_TRANSACTIONS = 2;
    private static final int POSITION_CATEGORIES_REPORT = 3;

    public OverviewCardsAdapter(Context context)
    {
        super(context);
    }

    @Override
    public int getViewTypeCount()
    {
        return 4;
    }

    @Override
    public int getItemViewType(int position)
    {
        return getDesiredPositionForId(cardInfoArray.get(position).getId());
    }

    @Override
    public void addCardInfo(CardView.CardInfo cardInfo)
    {
        final int currentPosition = cardInfoArray.indexOf(cardInfo);
        if (currentPosition >= 0)
            cardInfoArray.remove(currentPosition);

        // Find desired position
        final int desiredPosition = getDesiredPositionForId(cardInfo.getId());

        // Find actual position
        int position = 0;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < cardInfoArray.size(); i++)
        {
            if (desiredPosition < getDesiredPositionForId(cardInfoArray.get(i).getId()))
                break;
            position++;
        }

        super.addCardInfo(position, cardInfo);
    }

    @Override
    protected CardView newView(Context context, int position, CardView.CardInfo cardInfo, ViewGroup parent)
    {
        final long id = cardInfo.getId();

        if (id == AccountsCardView.UNIQUE_CARD_ID)
            return new AccountsCardView(context);
        else if (id == TransactionsCardView.UNIQUE_CARD_ID)
            return new TransactionsCardView(context);
        else if (id == CategoriesReportCardView.UNIQUE_CARD_ID)
            return new CategoriesReportCardView(context);
        else if (id == CreateAccountCardView.UNIQUE_CARD_ID)
            return new CreateAccountCardView(context);
        else
            return null;
    }

    private int getDesiredPositionForId(long id)
    {
        if (id == AccountsCardView.UNIQUE_CARD_ID)
            return POSITION_ACCOUNTS;
        else if (id == TransactionsCardView.UNIQUE_CARD_ID)
            return POSITION_TRANSACTIONS;
        else if (id == CategoriesReportCardView.UNIQUE_CARD_ID)
            return POSITION_CATEGORIES_REPORT;
        else if (id == CreateAccountCardView.UNIQUE_CARD_ID)
            return POSITION_CREATE_ACCOUNT;
        else
            return -1;
    }
}