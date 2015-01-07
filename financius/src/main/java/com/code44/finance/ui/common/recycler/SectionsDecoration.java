package com.code44.finance.ui.common.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class SectionsDecoration {
    public static interface Adapter<VH extends RecyclerView.ViewHolder> {
        public long getHeaderId(VH viewHolder);

        public View onNewHeaderView(ViewGroup parent, VH viewHolder);

        public void onBindHeaderView(ViewGroup parent, VH viewHolder);
    }
}
