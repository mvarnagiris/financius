package com.code44.finance.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Cursor adapter that puts header view at the top of each section.
 *
 * @author Mantas Varnagiris
 */
public abstract class AbstractSectionedCursorAdapter extends AbstractCursorAdapter implements SectionIndexer
{
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_HEADER = 1;
    protected static final int TYPE_COUNT = 2;
    protected final List<SectionInfo> sectionsList;
    protected final boolean isExpandable;
    protected final boolean useFirstAsHeader;
    private DataSetObserver mDataSetObserver = new DataSetObserver()
    {
        public void onChanged()
        {
            prepareIndexer(mCursor);
        }

        public void onInvalidated()
        {
            prepareIndexer(mCursor);
        }
    };


    public AbstractSectionedCursorAdapter(Context context, Cursor c)
    {
        this(context, c, false);
    }

    public AbstractSectionedCursorAdapter(Context context, Cursor c, boolean isExpandable)
    {
        this(context, c, isExpandable, false);
    }

    public AbstractSectionedCursorAdapter(Context context, Cursor c, boolean isExpandable, boolean useFirstAsHeader)
    {
        super(context, c);
        this.sectionsList = new ArrayList<SectionInfo>();
        this.isExpandable = isExpandable;
        this.useFirstAsHeader = useFirstAsHeader;
        if (c != null)
        {
            c.registerDataSetObserver(mDataSetObserver);
            findIndexes(c);
        }
        prepareIndexer(c);
    }

    protected abstract boolean isSectionExpanded(int section, Cursor c);

    protected abstract boolean onToggleSection(int section, boolean isExpanded, Cursor c);

    protected abstract String getIndexColumnValue(Cursor c);

    protected abstract String getRowSectionUniqueId(Cursor c);

    protected abstract View newHeaderView(Context context, int section, Cursor c, ViewGroup root);

    protected abstract void bindHeaderView(View view, Context context, int section, Cursor c);

    @Override
    public Cursor swapCursor(Cursor newCursor)
    {
        if (getCursor() != null)
            getCursor().unregisterDataSetObserver(mDataSetObserver);

        final Cursor oldCursor = super.swapCursor(newCursor);

        if (newCursor != null)
        {
            findIndexes(newCursor);
            newCursor.registerDataSetObserver(mDataSetObserver);
        }
        prepareIndexer(newCursor);

        return oldCursor;
    }

    @Override
    public int getCount()
    {
        int collapsedSize = 0;
        if (isExpandable)
        {
            for (SectionInfo section : sectionsList)
            {
                if (!section.isExpanded)
                    collapsedSize += section.size;
            }
        }

        return super.getCount() + (useFirstAsHeader ? 0 : sectionsList.size()) - collapsedSize;
    }

    @Override
    public Object getItem(int position)
    {
        return super.getItem(getCursorPosition(position));
    }

    // Public methods
    // ------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public long getItemId(int position)
    {
        return super.getItemId(getCursorPosition(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int type = getItemViewType(position);

        // Header views are handled by subclass
        if (type == TYPE_HEADER)
        {
            final int section = getSectionForPosition(position);
            mCursor.moveToPosition(getCursorPosition(position));
            if (convertView == null)
                convertView = newHeaderView(mContext, section, mCursor, parent);

            bindHeaderView(convertView, mContext, section, mCursor);
            return convertView;
        }

        // Normal items are handled by parent class
        return super.getView(getCursorPosition(position), convertView, parent);
    }

    // Private methods
    // ------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isEnabled(int position)
    {
        if (getItemViewType(position) == TYPE_HEADER)
            return isExpandable || useFirstAsHeader;

        return super.isEnabled(getCursorPosition(position));
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == getPositionForSection(getSectionForPosition(position)))
            return TYPE_HEADER;

        return TYPE_NORMAL;
    }

    // Abstract methods
    // ------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public int getViewTypeCount()
    {
        return TYPE_COUNT;
    }

    public boolean toggleSection(int position)
    {
        if (getItemViewType(position) == TYPE_HEADER)
        {
            final int section = getSectionForPosition(position);
            final int sectionPosition = getPositionForSection(section);
            final SectionInfo sectionInfo = sectionsList.get(section);
            mCursor.moveToPosition(getCursorPosition(sectionPosition));
            sectionInfo.isExpanded = onToggleSection(section, sectionInfo.isExpanded, mCursor);
            notifyDataSetChanged();
            return true;
        }

        return false;
    }

    public boolean isLastInSection(int position)
    {
        final int section = getSectionForPosition(position);
        final SectionInfo sectionInfo = sectionsList.get(section);
        return !sectionInfo.isExpanded || getPositionForSection(section) + sectionInfo.size == position;
    }

    @Override
    public Object[] getSections()
    {
        String[] sections = new String[sectionsList.size()];
        for (int i = 0; i < sectionsList.size(); i++)
            sections[i] = sectionsList.get(i).title;
        return sections;
    }

    @Override
    public int getPositionForSection(int section)
    {
        if (section >= sectionsList.size())
            return 0;

        int collapsedRows = 0;
        if (isExpandable)
        {
            SectionInfo sectionInfo;
            for (int i = 0; i < section; i++)
            {
                sectionInfo = sectionsList.get(i);
                if (!sectionInfo.isExpanded)
                    collapsedRows += sectionInfo.size;
            }
        }
        return sectionsList.get(section).position - collapsedRows;
    }

    @Override
    public int getSectionForPosition(int position)
    {
        int section = 0;
        SectionInfo sectionInfo;
        int totalSize = 0;
        for (int i = 0; i < sectionsList.size(); i++)
        {
            sectionInfo = sectionsList.get(i);
            totalSize += 1 + (sectionInfo.isExpanded || !isExpandable ? sectionInfo.size : 0);

            if (position < totalSize)
                return section;

            section++;
        }

        return section;
    }

    // SectionIndexer
    // ------------------------------------------------------------------------------------------------------------------------------------

    public int getCursorPosition(int position)
    {
        boolean isHeader = getItemViewType(position) == TYPE_HEADER;
        final int section = getSectionForPosition(position);
        int collapsedRows = 0;
        if (isExpandable)
        {
            SectionInfo sectionInfo;
            for (int i = 0; i < section; i++)
            {
                sectionInfo = sectionsList.get(i);
                if (!sectionInfo.isExpanded)
                    collapsedRows += sectionInfo.size;
            }
        }
        return position + collapsedRows - (useFirstAsHeader ? 0 : isHeader ? section : section + 1);
    }

    protected int getAdapterViewPosition(int cursorPosition)
    {
        int totalSize = 0;
        int collapsedSize = 0;
        int sectionsCount = 0;
        for (SectionInfo section : sectionsList)
        {
            sectionsCount++;
            totalSize += section.size;
            collapsedSize += section.isExpanded ? 0 : section.size;
            if (totalSize > cursorPosition)
                break;
        }

        return cursorPosition - collapsedSize + (useFirstAsHeader ? 0 : sectionsCount);
    }

    /**
     * Prepares sections.
     *
     * @param c
     */
    private void prepareIndexer(Cursor c)
    {
        sectionsList.clear();

        if (c == null || !c.moveToFirst())
            return;

        final Set<String> sectionsUniqueIDsSet = new HashSet<String>();

        int size = 0;
        String parsedSectionValue;
        do
        {
            if (sectionsUniqueIDsSet.add(getRowSectionUniqueId(c)))
            {
                parsedSectionValue = getIndexColumnValue(c);
                if (TextUtils.isEmpty(parsedSectionValue))
                    parsedSectionValue = "";

                final int sectionListSize = sectionsList.size();
                if (sectionListSize > 0)
                {
                    final SectionInfo sectionInfo = sectionsList.get(sectionListSize - 1);
                    sectionInfo.size = size + (useFirstAsHeader ? -1 : 0);
                    size = 0;
                }

                final boolean isSectionExpanded = isSectionExpanded(sectionListSize, c);
                sectionsList.add(new SectionInfo(parsedSectionValue, c.getPosition() + (useFirstAsHeader ? 0 : sectionsList.size()), isSectionExpanded));
            }
            size++;
        }
        while (c.moveToNext());

        final int sectionListSize = sectionsList.size();
        if (sectionListSize > 0)
        {
            final SectionInfo sectionInfo = sectionsList.get(sectionListSize - 1);
            sectionInfo.size = size + (useFirstAsHeader ? -1 : 0);
        }
    }

    // SectionInfo
    // ------------------------------------------------------------------------------------------------------------------------------------

    private static class SectionInfo
    {
        public final String title;
        public final int position;
        public boolean isExpanded;
        public int size;

        public SectionInfo(String title, int position, boolean isExpanded)
        {
            this.title = title;
            this.position = position;
            this.isExpanded = isExpanded;
            this.size = 0;
        }
    }
}