package com.code44.finance.ui;

import com.code44.finance.db.model.BaseModel;

public abstract class ModelEditFragment<T extends BaseModel> extends ModelFragment<T> {
    public abstract boolean onSave();
}
