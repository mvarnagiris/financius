package com.code44.finance.ui.common.recycler;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ClickViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private final OnItemClickListener onItemClickListener;
    private final OnItemLongClickListener onItemLongClickListener;

    public ClickViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
        this(itemView, onItemClickListener, null);
    }

    public ClickViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener, @Nullable OnItemLongClickListener onItemLongClickListener) {
        super(itemView);
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
        itemView.setOnClickListener(this);
        if (onItemLongClickListener != null) {
            itemView.setOnLongClickListener(this);
        }
    }

    @Override public void onClick(View view) {
        if (onItemClickListener != null) {
            final int position = getPosition();
            if (position >= 0) {
                onItemClickListener.onItemClick(view, position);
            }
        }
    }

    @Override public boolean onLongClick(View view) {
        if (onItemLongClickListener != null) {
            final int position = getPosition();
            if (position >= 0) {
                onItemLongClickListener.onItemLongClick(view, position);
                return true;
            }
        }
        return false;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View view, int position);
    }
}
