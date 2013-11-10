package com.code44.finance.ui;

import android.os.Bundle;

import java.util.List;

public abstract class DeleteItemsFragment extends AbstractFragment
{
    protected static final String ARG_ITEM_IDS = DeleteItemsFragment.class.getName() + ".ARG_ITEM_IDS";
    protected static final String ARG_ITEM_TITLES = DeleteItemsFragment.class.getName() + ".ARG_ITEM_TITLES";
    protected List<ItemToDelete> itemList;

    protected static Bundle makeArgs(ItemToDelete[] itemArray)
    {
        final long[] itemIDs = new long[itemArray.length];
        final String[] itemTitles = new String[itemArray.length];

        for (int i = 0; i < itemArray.length; i++)
        {
            itemIDs[i] = itemArray[i].getId();
            itemTitles[i] = itemArray[i].getTitle();
        }

        final Bundle args = new Bundle();
        args.putLongArray(ARG_ITEM_IDS, itemIDs);
        args.putStringArray(ARG_ITEM_TITLES, itemTitles);

        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get arguments
        final Bundle args = getArguments();
        final long[] itemIDs = args.getLongArray(ARG_ITEM_IDS);
        final String[] itemTitles = args.getStringArray(ARG_ITEM_TITLES);

        // Build itemList
        for (int i = 0; i < itemIDs.length; i++)
            itemList.add(new ItemToDelete(itemIDs[i], itemTitles[i]));
    }

    public static class ItemToDelete
    {
        final long id;
        final String title;

        public ItemToDelete(long id, String title)
        {
            this.id = id;
            this.title = title;
        }

        public long getId()
        {
            return id;
        }

        public String getTitle()
        {
            return title;
        }
    }

    protected static class ReferenceToCheck
    {

    }
}