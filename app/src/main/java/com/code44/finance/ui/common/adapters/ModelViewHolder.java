package com.code44.finance.ui.common.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.code44.finance.ui.common.recycler.ClickViewHolder;

public abstract class ModelViewHolder extends ClickViewHolder {
    public ModelViewHolder(@NonNull View itemView, @Nullable OnItemClickListener onItemClickListener) {
        super(itemView, onItemClickListener);
    }
}
