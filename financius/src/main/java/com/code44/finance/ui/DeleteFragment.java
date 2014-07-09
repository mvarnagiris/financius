package com.code44.finance.ui;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;

public class DeleteFragment extends BaseFragment implements View.OnClickListener {
    private static final String ARG_MAIN_DELETE_URI = "ARG_MAIN_DELETE_URI";

    private static final String FRAGMENT_DELETE = DeleteFragment.class.getName() + ".FRAGMENT_DELETE";

    private static final int LOADER_CURRENCIES = 1;
    private static final int LOADER_ACCOUNTS = 2;
    private static final int LOADER_CATEGORIES = 3;
    private static final int LOADER_TRANSACTIONS = 4;

    private TextView currencies_TV;
    private TextView accounts_TV;
    private TextView categories_TV;
    private TextView transactions_TV;

    private Uri mainDeleteUri;

    public static void show(FragmentActivity activity, int containerId, Uri mainDeleteUri) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .add(containerId, newInstance(mainDeleteUri), FRAGMENT_DELETE)
                .commit();
    }

    private static DeleteFragment newInstance(Uri mainDeleteUri) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_MAIN_DELETE_URI, mainDeleteUri);

        final DeleteFragment fragment = new DeleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        mainDeleteUri = getArguments().getParcelable(ARG_MAIN_DELETE_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delete, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        currencies_TV = (TextView) view.findViewById(R.id.currencies_TV);
        accounts_TV = (TextView) view.findViewById(R.id.accounts_TV);
        categories_TV = (TextView) view.findViewById(R.id.categories_TV);
        transactions_TV = (TextView) view.findViewById(R.id.transactions_TV);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onPause() {
        super.onPause();
        commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.undo_B:
//                undo();
//                break;
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

//    private void dismiss() {
//        getActivity().getSupportFragmentManager().popBackStack();
//    }
}