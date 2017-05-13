package com.code44.finance.ui.common.adapters;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.code44.finance.data.model.Model;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.recycler.ClickViewHolder;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ModelsAdapter<M extends Model, VH extends ModelViewHolder> extends RecyclerView.Adapter<VH> implements ClickViewHolder.OnItemClickListener {
    private final Set<M> selectedModels = new HashSet<>();
    private final OnModelClickListener<M> onModelClickListener;
    private final ModelsActivity.Mode mode;

    private Cursor cursor;

    public ModelsAdapter(@Nullable OnModelClickListener<M> onModelClickListener, @NonNull ModelsActivity.Mode mode) {
        this.onModelClickListener = onModelClickListener;
        this.mode = checkNotNull(mode, "Mode cannot be null.");
    }

    @Override public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        cursor.moveToPosition(position);
        final M model = modelFromCursor(cursor);
        onBindViewHolder(holder, position, model, mode != ModelsActivity.Mode.View && selectedModels.contains(model));
    }

    @Override public void onItemClick(View view, int position) {
        if (onModelClickListener != null) {
            cursor.moveToPosition(position);
            final M model = modelFromCursor(cursor);
            onModelClickListener.onModelClick(model, position);
        }
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, int position, @NonNull M model, boolean isSelected);

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        if (cursor != null) {
            cursor.moveToFirst();
        }
        notifyDataSetChanged();
    }

    public void toggleModelSelected(M model, int position) {
        if (!selectedModels.add(model)) {
            selectedModels.remove(model);
        }
        notifyItemChanged(position);
    }

    public Set<M> getSelectedModels() {
        return selectedModels;
    }

    public void setSelectedModels(Set<M> selectedModels) {
        this.selectedModels.clear();
        if (selectedModels != null) {
            this.selectedModels.addAll(selectedModels);
        }
        notifyDataSetChanged();
    }

    protected abstract M modelFromCursor(Cursor cursor);

    protected ModelsActivity.Mode getMode() {
        return mode;
    }

    public interface OnModelClickListener<M extends Model> {
        void onModelClick(M model, int position);
    }
}
