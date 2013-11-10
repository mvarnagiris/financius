package com.code44.finance.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractSectionedBaseAdapter extends BaseAdapter implements SectionIndexer
{
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_HEADER = 1;

    protected static final int TYPE_COUNT = 2;

    protected final Context context;
    protected final List<SectionInfo> sectionsList;
    protected final boolean isExpandable;
    protected final boolean useFirstAsHeader;
    protected List<SectionedBaseAdapterObject> objectList = new ArrayList<SectionedBaseAdapterObject>();

    public AbstractSectionedBaseAdapter(Context context, List<SectionedBaseAdapterObject> objectList, boolean isExpandable, boolean useFirstAsHeader)
    {
        this.context = context.getApplicationContext();
        if (objectList != null)
            this.objectList.addAll(objectList);
        this.sectionsList = new ArrayList<SectionInfo>();
        this.isExpandable = isExpandable;
        this.useFirstAsHeader = useFirstAsHeader;
        prepareIndexer();
    }

    // BaseAdapter
    // ------------------------------------------------------------------------------------------------------------------------------------

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

        return objectList.size() + (useFirstAsHeader ? 0 : sectionsList.size()) - collapsedSize;
    }

    @Override
    public int getViewTypeCount()
    {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == getPositionForSection(getSectionForPosition(position)))
            return TYPE_HEADER;

        return TYPE_NORMAL;
    }

    @Override
    public boolean isEnabled(int position)
    {
        if (getItemViewType(position) == TYPE_HEADER)
            return isExpandable || useFirstAsHeader;

        return super.isEnabled(getObjectListPosition(position));
    }

    @Override
    public Object getItem(int position)
    {
        return objectList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return objectList.get(position).getObjectId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final int type = getItemViewType(position);
        final int realPosition = getObjectListPosition(position);
        final SectionedBaseAdapterObject object = objectList.get(realPosition);
        final int section = getSectionForPosition(position);

        if (type == TYPE_HEADER)
        {
            if (convertView == null)
                convertView = newHeaderView(context, section, realPosition, object, parent);

            bindHeaderView(convertView, context, section, realPosition, object);
            return convertView;
        }
        else
        {
            if (convertView == null)
                convertView = newView(context, section, realPosition, object, parent);

            bindView(convertView, context, section, realPosition, object);
            return convertView;
        }
    }

    // Public methods
    // --------------------------------------------------------------------------------------------------------------------------------

    public void setData(List<? extends SectionedBaseAdapterObject> objectList)
    {
        this.objectList.clear();
        if (objectList != null)
            this.objectList.addAll(objectList);
        prepareIndexer();
        notifyDataSetChanged();
    }

    public boolean toggleSection(int position)
    {
        if (getItemViewType(position) == TYPE_HEADER)
        {
            final int section = getSectionForPosition(position);
            final int sectionPosition = getPositionForSection(section);
            final SectionInfo sectionInfo = sectionsList.get(section);
            sectionInfo.isExpanded = onToggleSection(section, sectionInfo.isExpanded, objectList.get(getObjectListPosition(sectionPosition)));
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

    // Protected methods
    // --------------------------------------------------------------------------------------------------------------------------------

    protected int getObjectListPosition(int position)
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

    protected int getAdapterViewPosition(int objectListPosition)
    {
        int totalSize = 0;
        int collapsedSize = 0;
        int sectionsCount = 0;
        for (SectionInfo section : sectionsList)
        {
            sectionsCount++;
            totalSize += section.size;
            collapsedSize += section.isExpanded ? 0 : section.size;
            if (totalSize > objectListPosition)
                break;
        }

        return objectListPosition - collapsedSize + sectionsCount;
    }

    // Private methods
    // ------------------------------------------------------------------------------------------------------------------------------------

    private void prepareIndexer()
    {
        sectionsList.clear();

        final Set<String> sectionsUniqueIDsSet = new HashSet<String>();

        int size = 0;
        String parsedSectionValue;
        int position = 0;
        for (SectionedBaseAdapterObject object : objectList)
        {
            if (sectionsUniqueIDsSet.add(object.getObjectSectionUniqueId()))
            {
                parsedSectionValue = object.getIndexValue();
                if (TextUtils.isEmpty(parsedSectionValue))
                    parsedSectionValue = "";

                final int sectionListSize = sectionsList.size();
                if (sectionListSize > 0)
                {
                    final SectionInfo sectionInfo = sectionsList.get(sectionListSize - 1);
                    sectionInfo.size = size + (useFirstAsHeader ? -1 : 0);
                    size = 0;
                }

                final boolean isSectionExpanded = isSectionExpanded(sectionListSize, object);
                sectionsList.add(new SectionInfo(parsedSectionValue, position + (useFirstAsHeader ? 0 : sectionsList.size()), isSectionExpanded));
            }
            size++;
            position++;
        }

        final int sectionListSize = sectionsList.size();
        if (sectionListSize > 0)
        {
            final SectionInfo sectionInfo = sectionsList.get(sectionListSize - 1);
            sectionInfo.size = size + (useFirstAsHeader ? -1 : 0);
        }
    }

    // Abstract methods
    // ------------------------------------------------------------------------------------------------------------------------------------

    protected abstract boolean isSectionExpanded(int section, SectionedBaseAdapterObject object);

    protected abstract boolean onToggleSection(int section, boolean isExpanded, SectionedBaseAdapterObject object);

    protected abstract View newHeaderView(Context context, int section, int position, SectionedBaseAdapterObject object, ViewGroup root);

    protected abstract void bindHeaderView(View view, Context context, int section, int position, SectionedBaseAdapterObject object);

    protected abstract View newView(Context context, int section, int position, SectionedBaseAdapterObject object, ViewGroup root);

    protected abstract void bindView(View view, Context context, int section, int position, SectionedBaseAdapterObject object);

    // SectionIndexer
    // ------------------------------------------------------------------------------------------------------------------------------------

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

    @Override
    public Object[] getSections()
    {
        String[] sections = new String[sectionsList.size()];
        for (int i = 0; i < sectionsList.size(); i++)
            sections[i] = sectionsList.get(i).title;
        return sections;
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

    // SectionedBaseAdapterObject
    // ------------------------------------------------------------------------------------------------------------------------------------

    public static abstract class SectionedBaseAdapterObject
    {
        protected long objectId;

        // Public methods
        // ------------------------------------------------------------------------------------------------------------------------------------

        public void setObjectId(long objectId)
        {
            this.objectId = objectId;
        }

        public long getObjectId()
        {
            return objectId;
        }

        // Abstract methods
        // ------------------------------------------------------------------------------------------------------------------------------------

        public abstract String getObjectSectionUniqueId();

        public abstract String getIndexValue();
    }
}