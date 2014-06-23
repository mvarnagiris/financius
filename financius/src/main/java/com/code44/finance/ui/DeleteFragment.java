package com.code44.finance.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.code44.finance.R;

public class DeleteFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_URI = "ARG_URI";

    private static final String FRAGMENT_DELETE = DeleteFragment.class.getName() + ".FRAGMENT_DELETE";

    public static void show(FragmentActivity activity, int containerId, Uri uri) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(containerId, newInstance(uri), FRAGMENT_DELETE)
                .commit();
    }

    private static DeleteFragment newInstance(Uri uri) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        final DeleteFragment fragment = new DeleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        Button undo_B = (Button) view.findViewById(R.id.undo_B);

        // Setup
        undo_B.setOnClickListener(this);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    // This will trigger onDestroy() and changes will be committed
                    dismiss();
                }
                return false;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo_B:
                undo();
                break;
        }
    }

    private void commit() {
//        ContentValues values = new ContentValues();
//        values.put(Tables.Task.ITEM_STATE.getName(), BaseModel.ItemState.DELETED.asString());
//        Uri uri = TasksProvider.addParam(TasksProvider.uriTasks(), BaseProvider.UriParams.KEY_NOTIFY_URI_CHANGED, BaseProvider.UriParams.VALUE_NOTIFY_URI_CHANGED_FALSE);
//        DataUtils.update(uri, values, Tables.Task.ITEM_STATE + "=?", new String[]{BaseModel.ItemState.DELETED_UNDO.asString()});
        dismiss();
    }

    private void undo() {
//        ContentValues values = new ContentValues();
//        values.put(Tables.Task.ITEM_STATE.getName(), BaseModel.ItemState.NORMAL.asString());
//        DataUtils.update(TasksProvider.uriTasks(), values, Tables.Task.ITEM_STATE + "=?", new String[]{BaseModel.ItemState.DELETED_UNDO.asString()});
        dismiss();
    }

    private void dismiss() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}