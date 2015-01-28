package com.code44.finance.ui.common.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.utils.ThemeUtils;

public class DividerDecoration extends RecyclerView.ItemDecoration {
    private final Drawable dividerDrawable;
    private int paddingLeft;
    private boolean drawDividerStart = false;
    private boolean drawDividerMiddle = true;
    private boolean drawDividerEnd = true;

    public DividerDecoration(Context context) {
        this(ThemeUtils.getDrawable(context, android.R.attr.dividerHorizontal));
    }

    public DividerDecoration(Drawable dividerDrawable) {
        this.dividerDrawable = dividerDrawable;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (isDrawDividerTop(view, parent)) {
            outRect.top += dividerDrawable.getIntrinsicHeight();
        }

        if (isDrawDividerBottom(view, parent)) {
            outRect.bottom += dividerDrawable.getIntrinsicHeight();
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
            final View view = parent.getChildAt(i);
            if (isDrawDividerTop(view, parent)) {
                drawDividerTop(c, view, layoutManager);
            }

            if (isDrawDividerBottom(view, parent)) {
                drawDividerBottom(c, view, layoutManager);
            }
        }
    }

    protected boolean isDrawDividerTop(View view, RecyclerView parent) {
        return drawDividerStart && hasViewBefore(view, parent);
    }

    protected boolean isDrawDividerBottom(View view, RecyclerView parent) {
        return (drawDividerMiddle && hasViewAfter(view, parent)) || (drawDividerEnd && !hasViewAfter(view, parent));
    }

    private void drawDividerTop(Canvas canvas, View view, RecyclerView.LayoutManager layoutManager) {
        final int left = layoutManager.getDecoratedLeft(view) + paddingLeft;
        final int right = layoutManager.getDecoratedRight(view);
        final int top = layoutManager.getDecoratedTop(view);
        final int bottom = top + dividerDrawable.getIntrinsicHeight();

        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(canvas);
    }

    private void drawDividerBottom(Canvas canvas, View view, RecyclerView.LayoutManager layoutManager) {
        final int left = layoutManager.getDecoratedLeft(view) + paddingLeft;
        final int right = layoutManager.getDecoratedRight(view);
        final int bottom = layoutManager.getDecoratedBottom(view);
        final int top = bottom - dividerDrawable.getIntrinsicHeight();

        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(canvas);
    }

    private boolean hasViewBefore(View view, RecyclerView parent) {
        // TODO Check for linear layout reversed
        return parent.getChildPosition(view) > 0;
    }

    private boolean hasViewAfter(View view, RecyclerView parent) {
        // TODO Check for linear layout reversed
        return parent.getChildPosition(view) < parent.getAdapter().getItemCount() - 1;
    }

    public DividerDecoration setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    public DividerDecoration setDrawDividerStart(boolean drawDividerStart) {
        this.drawDividerStart = drawDividerStart;
        return this;
    }

    public DividerDecoration setDrawDividerMiddle(boolean drawDividerMiddle) {
        this.drawDividerMiddle = drawDividerMiddle;
        return this;
    }

    public DividerDecoration setDrawDividerEnd(boolean drawDividerEnd) {
        this.drawDividerEnd = drawDividerEnd;
        return this;
    }
}
