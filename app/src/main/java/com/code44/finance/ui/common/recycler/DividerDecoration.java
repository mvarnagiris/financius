package com.code44.finance.ui.common.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.utils.ThemeUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class DividerDecoration extends RecyclerView.ItemDecoration {
    public static final int DRAW_DIVIDER_START = 1;
    public static final int DRAW_DIVIDER_MIDDLE = 2;
    public static final int DRAW_DIVIDER_END = 3;

    private final Drawable dividerDrawable;
    private final int paddingLeft;
    private final int paddingTop;
    private final int paddingRight;
    private final int paddingBottom;
    private final int drawDividerFlag;

    public DividerDecoration(@NonNull Context context) {
        this(context, 0, 0, 0, 0, DRAW_DIVIDER_MIDDLE | DRAW_DIVIDER_END);
    }

    public DividerDecoration(@NonNull Context context, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, @DrawDividerFlag int drawDividerFlag) {
        this(ThemeUtils.getDrawable(checkNotNull(context, "Context cannot be null."), android.R.attr.dividerHorizontal), paddingLeft, paddingTop, paddingRight, paddingBottom, drawDividerFlag);
    }

    public DividerDecoration(@NonNull Drawable dividerDrawable, int paddingLeft, int paddingTop, int paddingRight, int paddingBottom, @DrawDividerFlag int drawDividerFlag) {
        this.dividerDrawable = checkNotNull(dividerDrawable, "Divider Drawable cannot be null.");
        checkArgument(paddingLeft >= 0, "Padding left must be >= 0.");
        checkArgument(paddingTop >= 0, "Padding top must be >= 0.");
        checkArgument(paddingRight >= 0, "Padding right must be >= 0.");
        checkArgument(paddingBottom >= 0, "Padding bottom must be >= 0.");
        this.paddingLeft = paddingLeft;
        this.paddingTop = paddingTop;
        this.paddingRight = paddingRight;
        this.paddingBottom = paddingBottom;
        this.drawDividerFlag = drawDividerFlag;
    }

    @Override public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        final int position = parent.getChildPosition(view);
        final int itemCount = parent.getAdapter().getItemCount();
        final int drawableSize = dividerDrawable.getIntrinsicHeight();

        if (isDrawDividerTop(position, itemCount, view, parent)) {
            applyOffsetTop(outRect, drawableSize, paddingTop, paddingBottom, position, itemCount);
        }

        if (isDrawDividerBottom(position, itemCount, view, parent)) {
            applyOffsetBottom(outRect, drawableSize, paddingTop, paddingBottom, position, itemCount);
        }
    }

    @Override public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        final int itemCount = parent.getAdapter().getItemCount();
        for (int i = 0, childCount = parent.getChildCount(); i < childCount; i++) {
            final View view = parent.getChildAt(i);
            final int position = parent.getChildPosition(view);

            if (isDrawDividerTop(position, itemCount, view, parent)) {
                drawDividerTop(c, view, layoutManager);
            }

            if (isDrawDividerBottom(position, itemCount, view, parent)) {
                drawDividerBottom(c, view, layoutManager);
            }
        }
    }

    protected boolean isDrawDividerTop(int position, int itemCount, View view, RecyclerView parent) {
        return isDrawDividerFlagSet(DRAW_DIVIDER_START) && position == 0;
    }

    protected boolean isDrawDividerBottom(int position, int itemCount, View view, RecyclerView parent) {
        return isDrawDividerFlagSet(DRAW_DIVIDER_MIDDLE) && position < itemCount - 1 || isDrawDividerFlagSet(DRAW_DIVIDER_END) && position == itemCount - 1;
    }

    protected boolean isDrawDividerFlagSet(@DrawDividerFlag int drawDividerFlag) {
        return (this.drawDividerFlag & drawDividerFlag) == drawDividerFlag;
    }

    protected void applyOffsetTop(Rect outRect, int drawableSize, int paddingTop, int paddingBottom, int position, int itemCount) {
        outRect.top += drawableSize + paddingTop + paddingBottom;
    }

    protected void applyOffsetBottom(Rect outRect, int drawableSize, int paddingTop, int paddingBottom, int position, int itemCount) {
        outRect.bottom += drawableSize + paddingTop + paddingBottom;
    }

    private void drawDividerTop(Canvas canvas, View view, RecyclerView.LayoutManager layoutManager) {
        final int left = layoutManager.getDecoratedLeft(view) + paddingLeft;
        final int right = layoutManager.getDecoratedRight(view) - paddingRight;
        final int top = layoutManager.getDecoratedTop(view) + paddingTop;
        final int bottom = top + dividerDrawable.getIntrinsicHeight();

        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(canvas);
    }

    private void drawDividerBottom(Canvas canvas, View view, RecyclerView.LayoutManager layoutManager) {
        final int left = layoutManager.getDecoratedLeft(view) + paddingLeft;
        final int right = layoutManager.getDecoratedRight(view) - paddingRight;
        final int bottom = layoutManager.getDecoratedBottom(view) - paddingBottom;
        final int top = bottom - dividerDrawable.getIntrinsicHeight();

        dividerDrawable.setBounds(left, top, right, bottom);
        dividerDrawable.draw(canvas);
    }

    @Retention(RetentionPolicy.CLASS)
    @IntDef(flag = true, value = {DRAW_DIVIDER_START, DRAW_DIVIDER_MIDDLE, DRAW_DIVIDER_END})
    public @interface DrawDividerFlag {
    }
}
