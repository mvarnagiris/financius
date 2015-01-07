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

    public DividerDecoration(Context context) {
        this(ThemeUtils.getDrawable(context, android.R.attr.dividerHorizontal));
    }

    public DividerDecoration(Drawable dividerDrawable) {
        this.dividerDrawable = dividerDrawable;
    }

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom += dividerDrawable.getIntrinsicHeight();
    }

    @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
            drawDivider(c, parent.getChildAt(i), layoutManager);
        }
    }

    private void drawDivider(Canvas canvas, View view, RecyclerView.LayoutManager layoutManager) {
        final int left = layoutManager.getDecoratedLeft(view) + paddingLeft;
        final int right = layoutManager.getDecoratedRight(view);
        final int bottom = layoutManager.getDecoratedBottom(view);
        final int top = bottom - dividerDrawable.getIntrinsicHeight();

        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(canvas);
    }

    public DividerDecoration setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }
}
