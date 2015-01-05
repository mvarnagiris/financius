package com.code44.finance.ui.common.presenters;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;

import com.code44.finance.R;

public abstract class RecyclerViewPresenter<A extends RecyclerView.Adapter<?>> extends Presenter {
    private final RecyclerView recyclerView;
    private final A adapter;

    public RecyclerViewPresenter(ActionBarActivity activity) {
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
