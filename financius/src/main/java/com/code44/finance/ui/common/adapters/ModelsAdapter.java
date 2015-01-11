package com.code44.finance.ui.common.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.data.model.Model;
import com.code44.finance.ui.common.presenters.ModelsActivityPresenter;

import java.util.HashSet;
import java.util.Set;

public abstract class ModelsAdapter<M extends Model> extends RecyclerView.Adapter<ModelsAdapter.ModelViewHolder<M>> {
    private final Set<M> selectedModels = new HashSet<>();
    private final OnModelClickListener<M> onModelClickListener;

    private Cursor cursor;
    private ModelsActivityPresenter.Mode mode = ModelsActivityPresenter.Mode.View;

    public ModelsAdapter(OnModelClickListener<M> onModelClickListener) {
        this.onModelClickListener = onModelClickListener;
    }

    @Override public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override public ModelViewHolder<M> onCreateViewHolder(ViewGroup parent, int viewType) {
        final ModelViewHolder<M> modelViewHolder = createModelViewHolder(parent, viewType);
        modelViewHolder.setOnModelClickListener(onModelClickListener);
        return modelViewHolder;
    }

    @Override public void onBindViewHolder(ModelViewHolder<M> holder, int position) {
        cursor.moveToPosition(position);
        final M model = modelFromCursor(cursor);
        holder.bindViewHolder(model, cursor, position, mode, mode != ModelsActivityPresenter.Mode.View && selectedModels.contains(model));
    }

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

    public void setMode(ModelsActivityPresenter.Mode mode) {
        this.mode = mode;
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

    protected abstract ModelViewHolder<M> createModelViewHolder(ViewGroup parent, int viewType);

    protected abstract M modelFromCursor(Cursor cursor);

    public static interface OnModelClickListener<M extends Model> {
        public void onModelClick(View view, M model, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected);
    }

    public static abstract class ModelViewHolder<M extends Model> extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnModelClickListener<M> onModelClickListener;

        private M model;
        private Cursor cursor;
        private int position;
        private ModelsActivityPresenter.Mode mode;
        private boolean isSelected;

        public ModelViewHolder(View itemView) {
            super(itemView);
        }

        public void bindViewHolder(M model, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected) {
            this.model = model;
            this.cursor = cursor;
            this.position = position;
            this.mode = mode;
            this.isSelected = isSelected;
            bind(model, cursor, position, mode, isSelected);
        }

        public M getModel() {
            return model;
        }

        protected abstract void bind(M model, Cursor cursor, int position, ModelsActivityPresenter.Mode mode, boolean isSelected);

        private void setOnModelClickListener(OnModelClickListener<M> onModelClickListener) {
            this.onModelClickListener = onModelClickListener;
            if (onModelClickListener != null) {
                itemView.setOnClickListener(this);
            } else {
                itemView.setOnClickListener(null);
            }
        }

        @Override public void onClick(View v) {
            cursor.moveToPosition(position);
            onModelClickListener.onModelClick(v, model, cursor, position, mode, isSelected);
        }
    }
}
