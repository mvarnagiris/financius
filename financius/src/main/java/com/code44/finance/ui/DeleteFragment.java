package com.code44.finance.ui;

import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TransactionsProvider;

import de.greenrobot.event.EventBus;

public class DeleteFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String ARG_DELETE_URI = "ARG_DELETE_URI";
    private static final String ARG_DELETE_SELECTION = "ARG_DELETE_SELECTION";
    private static final String ARG_DELETE_SELECTION_ARGS = "ARG_DELETE_SELECTION_ARGS";

    private static final String FRAGMENT_DELETE = DeleteFragment.class.getName() + ".FRAGMENT_DELETE";

    private static final int LOADER_CURRENCIES = 1;
    private static final int LOADER_ACCOUNTS = 2;
    private static final int LOADER_CATEGORIES = 3;
    private static final int LOADER_TRANSACTIONS = 4;

    private TextView currencies_TV;
    private TextView accounts_TV;
    private TextView categories_TV;
    private TextView transactions_TV;

    private Uri deleteUri;
    private String deleteSelection;
    private String[] deleteSelectionArgs;

    public static void show(FragmentManager fm, Uri deleteUri, String deleteSelection, String... deleteSelectionArgs) {
        newInstance(deleteUri, deleteSelection, deleteSelectionArgs).show(fm, FRAGMENT_DELETE);
    }

    private static DeleteFragment newInstance(Uri deleteUri, String deleteSelection, String... deleteSelectionArgs) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_DELETE_URI, deleteUri);
        args.putString(ARG_DELETE_SELECTION, deleteSelection);
        args.putStringArray(ARG_DELETE_SELECTION_ARGS, deleteSelectionArgs);

        final DeleteFragment fragment = new DeleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        final Bundle args = getArguments();
        deleteUri = args.getParcelable(ARG_DELETE_URI);
        deleteSelection = args.getString(ARG_DELETE_SELECTION);
        deleteSelectionArgs = args.getStringArray(ARG_DELETE_SELECTION_ARGS);

        // Delete
        if (savedInstanceState == null) {
            DataStore.delete().selection(deleteSelection, deleteSelectionArgs).from(deleteUri);
        }
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
        final Button cancel_B = (Button) view.findViewById(R.id.cancel_B);
        final Button save_B = (Button) view.findViewById(R.id.save_B);

        // Setup
        cancel_B.setOnClickListener(this);
        save_B.setOnClickListener(this);
        save_B.setText(R.string.delete);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loaders
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
        getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
        getLoaderManager().initLoader(LOADER_CATEGORIES, null, this);
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_B:
                cancel();
                break;

            case R.id.save_B:
                delete();
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_CURRENCIES:
                return Query.create()
                        .projectionId(Tables.Currencies.ID)
                        .selection(Tables.Currencies.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), CurrenciesProvider.uriCurrencies());

            case LOADER_ACCOUNTS:
                return Query.create()
                        .projectionId(Tables.Accounts.ID)
                        .selection(Tables.Accounts.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), AccountsProvider.uriAccounts());

            case LOADER_CATEGORIES:
                return Query.create()
                        .projectionId(Tables.Categories.ID)
                        .selection(Tables.Categories.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), CategoriesProvider.uriCategories());

            case LOADER_TRANSACTIONS:
                return Query.create()
                        .projectionId(Tables.Transactions.ID)
                        .selection(Tables.Transactions.ITEM_STATE + "=?", String.valueOf(BaseModel.ItemState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int count = cursor.getCount();
        String item;
        TextView view;
        switch (cursorLoader.getId()) {
            case LOADER_CURRENCIES:
                item = getResources().getQuantityString(R.plurals.currency, count);
                view = currencies_TV;
                break;

            case LOADER_ACCOUNTS:
                item = getResources().getQuantityString(R.plurals.account, count);
                view = accounts_TV;
                break;

            case LOADER_CATEGORIES:
                item = getResources().getQuantityString(R.plurals.category, count);
                view = categories_TV;
                break;

            case LOADER_TRANSACTIONS:
                item = getResources().getQuantityString(R.plurals.transaction, count);
                view = transactions_TV;
                break;

            default:
                return;
        }

        if (count == 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
            view.setText(getString(R.string.f_x_y_will_be_deleted, count, item));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        DataStore.undoDelete().selection(deleteSelection, deleteSelectionArgs).from(deleteUri);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        DataStore.undoDelete().selection(deleteSelection, deleteSelectionArgs).from(deleteUri);
    }

    private void cancel() {
        dismiss();
    }

    private void delete() {
        DataStore.commitDelete().selection(deleteSelection, deleteSelectionArgs).from(deleteUri);
        dismiss();
        EventBus.getDefault().post(new DeleteEvent(deleteUri, deleteSelection, deleteSelectionArgs));
    }

    public static class DeleteEvent {
        private final Uri uri;
        private final String selection;
        private final String[] selectionArgs;

        public DeleteEvent(Uri uri, String selection, String[] selectionArgs) {
            this.uri = uri;
            this.selection = selection;
            this.selectionArgs = selectionArgs;
        }

        public Uri getUri() {
            return uri;
        }

        public String getSelection() {
            return selection;
        }

        public String[] getSelectionArgs() {
            return selectionArgs;
        }
    }
}