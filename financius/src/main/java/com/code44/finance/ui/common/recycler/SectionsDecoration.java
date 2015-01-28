package com.code44.finance.ui.common.recycler;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class SectionsDecoration extends RecyclerView.ItemDecoration {
    private final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final Adapter adapter = getAdapter(parent);
        if (adapter == null) {
            return;
        }

        if (!hasHeader(adapter, view, parent)) {
            return;
        }

        final RecyclerView.ViewHolder headerViewHolder = getMeasuredAndBoundViewHolder(adapter, parent, parent.getChildViewHolder(view));
        outRect.top += headerViewHolder.itemView.getMeasuredHeight();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final Adapter adapter = getAdapter(parent);
        if (adapter == null) {
            return;
        }

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
            final View view = parent.getChildAt(i);
            if (!hasHeader(adapter, view, parent)) {
                continue;
            }

            drawHeader(c, getMeasuredAndBoundViewHolder(adapter, parent, parent.getChildViewHolder(view)), layoutManager, view);
        }
    }

    private Adapter getAdapter(RecyclerView parent) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof Adapter) {
            return (Adapter) adapter;
        }
        return null;
    }

    private boolean hasHeader(Adapter adapter, View view, RecyclerView parent) {
        final int adapterPosition = parent.getChildPosition(view);
        if (adapterPosition == 0) {
            return true;
        }

        final long currentHeaderId = adapter.getHeaderId(adapterPosition);
        final long previousHeaderId = adapter.getHeaderId(adapterPosition - 1);

        return currentHeaderId != previousHeaderId;
    }

    private RecyclerView.ViewHolder getMeasuredAndBoundViewHolder(Adapter adapter, RecyclerView parent, RecyclerView.ViewHolder viewHolder) {
        final RecyclerView.ViewHolder headerViewHolder = getHeaderViewHolder(adapter, parent, viewHolder);
        //noinspection unchecked
        adapter.onBindHeaderViewHolder(headerViewHolder, viewHolder.getPosition());
        headerViewHolder.itemView.measure(View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY), heightMeasureSpec);
        return headerViewHolder;
    }

    private RecyclerView.ViewHolder getHeaderViewHolder(Adapter adapter, RecyclerView parent, RecyclerView.ViewHolder viewHolder) {
        //noinspection unchecked
        return adapter.onCreateHeaderViewHolder(parent);
    }

    private void drawHeader(Canvas canvas, RecyclerView.ViewHolder headerViewHolder, RecyclerView.LayoutManager layoutManager, View view) {
        headerViewHolder.itemView.layout(0, 0, headerViewHolder.itemView.getMeasuredWidth(), headerViewHolder.itemView.getMeasuredHeight());

        final int top = layoutManager.getDecoratedTop(view);
        canvas.save();
        canvas.translate(0, top);
        headerViewHolder.itemView.draw(canvas);
        canvas.restore();
    }

    public static interface Adapter<VH extends RecyclerView.ViewHolder> {
        public long getHeaderId(int position);

        public VH onCreateHeaderViewHolder(ViewGroup parent);

        public void onBindHeaderViewHolder(VH viewHolder, int position);
    }
}
