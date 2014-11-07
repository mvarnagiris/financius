package com.code44.finance.ui.common;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;

import com.code44.finance.data.model.Model;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseModelsAdapter extends CursorAdapter {
    private final Set<Model> selectedModels = new HashSet<>();

    public BaseModelsAdapter(Context context) {
        super(context, null, true);
    }

    public Set<Model> getSelectedModels() {
        return selectedModels;
    }

    public void setSelectedModels(Set<? extends Model> selectedModels) {
        this.selectedModels.clear();
        if (selectedModels != null) {
            this.selectedModels.addAll(selectedModels);
        }
        notifyDataSetChanged();
    }

    public boolean isModelSelected(Model model) {
        return selectedModels.contains(model);
    }

    public void toggleModelSelected(Model model) {
        if (!selectedModels.add(model)) {
            selectedModels.remove(model);
        }
        notifyDataSetChanged();
    }
}
