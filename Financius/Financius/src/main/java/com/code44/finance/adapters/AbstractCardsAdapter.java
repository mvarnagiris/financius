package com.code44.finance.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.code44.finance.views.cards.CardView;

import java.util.ArrayList;

public abstract class AbstractCardsAdapter extends BaseAdapter
{
    protected final Context context;
    protected final ArrayList<CardView.CardInfo> cardInfoArray = new ArrayList<CardView.CardInfo>();

    protected AbstractCardsAdapter(Context context)
    {
        this.context = context;
    }

    protected abstract CardView newView(Context context, int position, CardView.CardInfo cardInfo, ViewGroup parent);

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public int getCount()
    {
        return cardInfoArray.size();
    }

    @Override
    public Object getItem(int position)
    {
        return cardInfoArray.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return cardInfoArray.get(position).getId();
    }

    public void addCardInfo(CardView.CardInfo cardInfo)
    {
        addCardInfo(cardInfoArray.size(), cardInfo);
    }

    public void addCardInfo(int position, CardView.CardInfo cardInfo)
    {
        final int currentPosition = cardInfoArray.indexOf(cardInfo);
        if (currentPosition >= 0)
            cardInfoArray.remove(currentPosition);

        cardInfoArray.add(Math.min(position, cardInfoArray.size()), cardInfo);
        notifyDataSetChanged();
    }

    public void removeCardInfo(long id)
    {
        final int currentPosition = cardInfoArray.indexOf(new CardView.CardInfo(id));
        if (currentPosition >= 0)
        {
            cardInfoArray.remove(currentPosition);
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final CardView.CardInfo cardInfo = cardInfoArray.get(position);
        if (convertView == null)
            convertView = newView(context, position, cardInfo, parent);
        bindView((CardView) convertView, position, cardInfo);
        return convertView;
    }

    protected void bindView(CardView view, @SuppressWarnings("UnusedParameters") int position, CardView.CardInfo cardInfo)
    {
        view.setCardInfo(cardInfo);
    }
}