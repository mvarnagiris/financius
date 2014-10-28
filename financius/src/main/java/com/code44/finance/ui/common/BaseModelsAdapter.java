package com.code44.finance.ui.common;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.code44.finance.data.model.BaseModel;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseModelsAdapter<M extends BaseModel, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected final Set<M> selectedModels = new HashSet<>();
    protected final SparseArrayCompat<M> modelCache = new SparseArrayCompat<>();
    protected final Context context;

    protected Cursor cursor;

    public BaseModelsAdapter(Context context) {
        this.context = context;
    }

    protected abstract VH createViewHolder(Context context, ViewGroup parent, int position);

    protected abstract void bindViewHolder(Context context, VH holder, int position, M model);

    protected abstract M createModel(Cursor cursor);

    @Override public VH onCreateViewHolder(ViewGroup parent, int position) {
        return createViewHolder(context, parent, position);
    }

    @Override public void onBindViewHolder(VH holder, int position) {
        bindViewHolder(context, holder, position, getModel(position));
    }

    @Override public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor) {
        invalidateModelCache();
        Cursor oldCursor = this.cursor;
        this.cursor = cursor;
        notifyDataSetChanged();
        return oldCursor;
    }

    public M getModel(int position) {
        M model = modelCache.get(position);
        if (model == null) {
            cursor.moveToPosition(position);
            model = createModel(cursor);
            modelCache.put(position, model);
        }
        return model;
    }

    protected void invalidateModelCache() {
        modelCache.clear();
    }
}
