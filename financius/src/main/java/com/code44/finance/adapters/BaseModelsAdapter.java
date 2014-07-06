package com.code44.finance.adapters;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;

public abstract class BaseModelsAdapter extends CursorAdapter {
    public BaseModelsAdapter(Context context) {
        super(context, null, true);
    }
}
