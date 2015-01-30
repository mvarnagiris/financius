package com.code44.finance.ui.common.presenters;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.code44.finance.R;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.ui.common.views.EmptyContainerView;

public abstract class RecyclerViewPresenter<A extends RecyclerView.Adapter<?>> extends ActivityPresenter implements EmptyContainerView.Callbacks {
    private RecyclerView recyclerView;
    private EmptyContainerView emptyContainerView;
    private A adapter;

    @Override public void onCreate(BaseActivity activity, Bundle savedInstanceState) {
        super.onCreate(activity, savedInstanceState);
        recyclerView = findView(activity, R.id.recyclerView);
        emptyContainerView = findView(activity, R.id.emptyContainerView);
        setupRecyclerView(recyclerView);
        adapter = createAdapter();
        recyclerView.setAdapter(adapter);

        if (emptyContainerView != null) {
            emptyContainerView.setCallbacks(this);
            adapter.registerAdapterDataObserver(new AdapterObserver());
        }
    }

    protected abstract void setupRecyclerView(RecyclerView recyclerView);

    protected abstract A createAdapter();

    protected RecyclerView getRecyclerView() {
        return recyclerView;
    }

    protected A getAdapter() {
        return adapter;
    }

    protected EmptyContainerView getEmptyContainerView() {
        return emptyContainerView;
    }

    private class AdapterObserver extends RecyclerView.AdapterDataObserver {
        @Override public void onChanged() {
            super.onChanged();
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }

        @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            emptyContainerView.setEmpty(adapter.getItemCount() == 0);
        }
    }
}
