package com.code44.finance.ui.common.recycler;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.util.LongSparseArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

public class SectionsDecoration extends RecyclerView.ItemDecoration {
    private final LongSparseArray<RecyclerView.ViewHolder> activeViewHolders = new LongSparseArray<>();
    private final Set<Long> drawnHeaderIds = new HashSet<>();
    private final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
    private final boolean isHeaderFixedSize;
    private final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    private int widthMeasureSpec;

    public SectionsDecoration(boolean isHeaderFixedSize) {
        this.isHeaderFixedSize = isHeaderFixedSize;
    }

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

        if (widthMeasureSpec == 0) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY);
        }

        final int position = parent.getChildPosition(view);
        outRect.top += getViewHolder(adapter, parent, adapter.getHeaderId(position), position).itemView.getMeasuredHeight();
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final Adapter adapter = getAdapter(parent);
        if (adapter == null) {
            return;
        }

        drawnHeaderIds.clear();
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
            final View view = parent.getChildAt(i);
            final int position = parent.getChildPosition(view);
            final long headerId = adapter.getHeaderId(position);
            if (!hasHeader(adapter, view, parent)) {
                if (!drawnHeaderIds.contains(headerId)) {
                    final RecyclerView.ViewHolder viewHolder = activeViewHolders.get(headerId);
                    if (viewHolder != null) {
                        activeViewHolders.remove(headerId);
                        recycledViewPool.putRecycledView(viewHolder);
                    }
                }
                continue;
            }

            drawnHeaderIds.add(headerId);
            drawHeader(c, getViewHolder(adapter, parent, headerId, position), layoutManager, view);
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

    private RecyclerView.ViewHolder getViewHolder(Adapter adapter, RecyclerView parent, long headerId, int position) {
        RecyclerView.ViewHolder viewHolder = activeViewHolders.get(headerId);
        if (viewHolder != null) {
            return viewHolder;
        }

        viewHolder = recycledViewPool.getRecycledView(-1);
        if (viewHolder != null) {
            if (isHeaderFixedSize) {
                bind(adapter, viewHolder, position);
                activeViewHolders.put(headerId, viewHolder);
                return viewHolder;
            }

            bind(adapter, viewHolder, position);
            measure(viewHolder);
            activeViewHolders.put(headerId, viewHolder);
            return viewHolder;
        }

        viewHolder = adapter.onCreateHeaderViewHolder(parent);
        bind(adapter, viewHolder, position);
        measure(viewHolder);
        activeViewHolders.put(headerId, viewHolder);
        return viewHolder;
    }

    private void bind(Adapter adapter, RecyclerView.ViewHolder viewHolder, int position) {
        //noinspection unchecked
        adapter.onBindHeaderViewHolder(viewHolder, position);
    }

    private void measure(RecyclerView.ViewHolder viewHolder) {
        viewHolder.itemView.measure(widthMeasureSpec, heightMeasureSpec);
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
