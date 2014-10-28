package com.code44.finance.adapters;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;

import com.code44.finance.data.model.BaseModel;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseModelsAdapterOld extends CursorAdapter {
    private final Set<BaseModel> selectedModels = new HashSet<>();

    public BaseModelsAdapterOld(Context context) {
        super(context, null, true);
    }

    public Set<BaseModel> getSelectedModels() {
        return selectedModels;
    }

    public void setSelectedModels(Set<? extends BaseModel> selectedModels) {
        this.selectedModels.clear();
        if (selectedModels != null) {
            this.selectedModels.addAll(selectedModels);
        }
        notifyDataSetChanged();
    }

    public boolean isModelSelected(BaseModel model) {
        return selectedModels.contains(model);
    }

    public void toggleModelSelected(BaseModel model) {
        if (!selectedModels.add(model)) {
            selectedModels.remove(model);
        }
        notifyDataSetChanged();
    }
}
