package com.code44.finance.ui.common.recycler;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class SectionsDecoration extends RecyclerView.ItemDecoration {
    private final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final Adapter adapter = getAdapter(parent);
        if (adapter == null) {
            return;
        }

        final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
        if (!hasHeader(adapter, view, viewHolder, parent)) {
            return;
        }

        final RecyclerView.ViewHolder headerViewHolder = getMeasuredAndBoundViewHolder(adapter, parent, viewHolder);
        outRect.top += headerViewHolder.itemView.getMeasuredHeight();
    }

    @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final Adapter adapter = getAdapter(parent);
        if (adapter == null) {
            return;
        }

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
            final View view = parent.getChildAt(i);
            final RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
            if (!hasHeader(adapter, view, viewHolder, parent)) {
                continue;
            }

            drawHeader(c, getMeasuredAndBoundViewHolder(adapter, parent, viewHolder), layoutManager, view);
        }
    }

    private Adapter getAdapter(RecyclerView parent) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter instanceof Adapter) {
            return (Adapter) adapter;
        }
        return null;
    }

    private boolean hasHeader(Adapter adapter, View view, RecyclerView.ViewHolder viewHolder, RecyclerView parent) {
        final int adapterPosition = parent.getChildPosition(view);
        if (adapterPosition == 0) {
            return true;
        }

        final int previousViewIndex = parent.indexOfChild(view) - 1;
        if (previousViewIndex < 0) {
            return false;
        }
        final View previousView = parent.getChildAt(previousViewIndex);

        //noinspection unchecked
        final long currentHeaderId = adapter.getHeaderId(viewHolder);
        //noinspection unchecked
        final long previousHeaderId = adapter.getHeaderId(parent.getChildViewHolder(previousView));

        return currentHeaderId != previousHeaderId;
    }

    private RecyclerView.ViewHolder getMeasuredAndBoundViewHolder(Adapter adapter, RecyclerView parent, RecyclerView.ViewHolder viewHolder) {
        final RecyclerView.ViewHolder headerViewHolder = getHeaderViewHolder(adapter, parent, viewHolder);
        //noinspection unchecked
        adapter.onBindHeaderViewHolder(headerViewHolder, viewHolder);
        headerViewHolder.itemView.measure(View.MeasureSpec.makeMeasureSpec(parent.getWidth(), View.MeasureSpec.EXACTLY), heightMeasureSpec);
        return headerViewHolder;
    }

    private RecyclerView.ViewHolder getHeaderViewHolder(Adapter adapter, RecyclerView parent, RecyclerView.ViewHolder viewHolder) {
        //noinspection unchecked
        return adapter.onCreateHeaderViewHolder(parent, 0, viewHolder);
    }

    private void drawHeader(Canvas canvas, RecyclerView.ViewHolder headerViewHolder, RecyclerView.LayoutManager layoutManager, View view) {
        headerViewHolder.itemView.layout(0, 0, headerViewHolder.itemView.getMeasuredWidth(), headerViewHolder.itemView.getMeasuredHeight());

        final int top = layoutManager.getDecoratedTop(view);
        canvas.save();
        canvas.translate(0, top);
        headerViewHolder.itemView.draw(canvas);
        canvas.restore();
    }

    public static interface Adapter<HVH extends RecyclerView.ViewHolder, VH extends RecyclerView.ViewHolder> {
        public long getHeaderId(VH viewHolder);

        public HVH onCreateHeaderViewHolder(ViewGroup parent, int viewType, VH viewHolder);

        public void onBindHeaderViewHolder(HVH headerViewHolder, VH viewHolder);
    }
}
