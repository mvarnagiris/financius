package com.code44.finance.ui.common.presenters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.code44.finance.R;
import com.code44.finance.ui.common.BaseActivity;

public abstract class RecyclerViewPresenter<A extends RecyclerView.Adapter<?>> extends ActivityPresenter {
    private RecyclerView recyclerView;
    private A adapter;

    @Override public void onActivityCreated(BaseActivity activity, Bundle savedInstanceState) {
        super.onActivityCreated(activity, savedInstanceState);
        recyclerView = findView(activity, R.id.recyclerView);
        setupRecyclerView(recyclerView);
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);
    }

    protected abstract void setupRecyclerView(RecyclerView recyclerView);

    protected abstract A createAdapter();

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected A getAdapter() {
        return adapter;
    }
}
