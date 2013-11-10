package com.code44.finance.adapters;

import android.content.Context;

public abstract class AbstractReportAdapter extends AbstractSectionedBaseAdapter
{
    public AbstractReportAdapter(Context context, boolean isExpandable, boolean useFirstAsHeader)
    {
        super(context, null, isExpandable, useFirstAsHeader);
    }

    // ReportObject
    // --------------------------------------------------------------------------------------------------------------------------------

    public static abstract class ReportObject extends SectionedBaseAdapterObject
    {
        public ReportObject(long objectId)
        {
            this.objectId = objectId;
        }

        // SectionedBaseAdapterObject
        // --------------------------------------------------------------------------------------------------------------------------------

        @Override
        public String getIndexValue()
        {
            // We don't use indexes yet
            return "";
        }
    }
}