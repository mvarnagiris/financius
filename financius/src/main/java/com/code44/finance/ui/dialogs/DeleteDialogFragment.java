package com.code44.finance.ui.dialogs;

import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.model.ModelState;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.providers.AccountsProvider;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.data.providers.CurrenciesProvider;
import com.code44.finance.data.providers.TagsProvider;
import com.code44.finance.data.providers.TransactionsProvider;

public class DeleteDialogFragment extends AlertDialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_DELETE_URI = "ARG_DELETE_URI";
    private static final String ARG_DELETE_SELECTION = "ARG_DELETE_SELECTION";
    private static final String ARG_DELETE_SELECTION_ARGS = "ARG_DELETE_SELECTION_ARGS";

    private static final int LOADER_CURRENCIES = 1;
    private static final int LOADER_ACCOUNTS = 2;
    private static final int LOADER_CATEGORIES = 3;
    private static final int LOADER_TAGS = 4;
    private static final int LOADER_TRANSACTIONS = 5;

    private TextView currencies_TV;
    private TextView accounts_TV;
    private TextView categories_TV;
    private TextView tags_TV;
    private TextView transactions_TV;

    private Uri deleteUri;
    private String deleteSelection;
    private String[] deleteSelectionArgs;

    public static DeleteDialogFragment newInstance(Context context, int requestCode, Uri deleteUri, String deleteSelection, String... deleteSelectionArgs) {
        return (DeleteDialogFragment) new Builder(context, requestCode, deleteUri, deleteSelection, deleteSelectionArgs).build();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        deleteUri = getArguments().getParcelable(ARG_DELETE_URI);
        deleteSelection = getArguments().getString(ARG_DELETE_SELECTION);
        deleteSelectionArgs = getArguments().getStringArray(ARG_DELETE_SELECTION_ARGS);

        // Delete
        if (savedInstanceState == null) {
            DataStore.delete().selection(deleteSelection, deleteSelectionArgs).from(getActivity(), deleteUri);
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);
        //noinspection ConstantConditions
        inflater.inflate(R.layout.include_delete_dialog, (ViewGroup) view.findViewById(R.id.container_V), true);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        currencies_TV = (TextView) view.findViewById(R.id.currencies_TV);
        accounts_TV = (TextView) view.findViewById(R.id.accounts_TV);
        categories_TV = (TextView) view.findViewById(R.id.categories_TV);
        tags_TV = (TextView) view.findViewById(R.id.tags_TV);
        transactions_TV = (TextView) view.findViewById(R.id.transactions_TV);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Loaders
        getLoaderManager().initLoader(LOADER_CURRENCIES, null, this);
        getLoaderManager().initLoader(LOADER_ACCOUNTS, null, this);
        getLoaderManager().initLoader(LOADER_CATEGORIES, null, this);
        getLoaderManager().initLoader(LOADER_TAGS, null, this);
        getLoaderManager().initLoader(LOADER_TRANSACTIONS, null, this);
    }

    @Override public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case LOADER_CURRENCIES:
                return Query.create()
                        .projectionId(Tables.Currencies.LOCAL_ID)
                        .selection(Tables.Currencies.MODEL_STATE + "=?", String.valueOf(ModelState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), CurrenciesProvider.uriCurrencies());

            case LOADER_ACCOUNTS:
                return Query.create()
                        .projectionId(Tables.Accounts.ID)
                        .selection(Tables.Accounts.MODEL_STATE + "=?", String.valueOf(ModelState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), AccountsProvider.uriAccounts());

            case LOADER_CATEGORIES:
                return Query.create()
                        .projectionId(Tables.Categories.ID)
                        .selection(Tables.Categories.MODEL_STATE + "=?", String.valueOf(ModelState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), CategoriesProvider.uriCategories());

            case LOADER_TAGS:
                return Query.create()
                        .projectionId(Tables.Tags.ID)
                        .selection(Tables.Tags.MODEL_STATE + "=?", String.valueOf(ModelState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), TagsProvider.uriTags());

            case LOADER_TRANSACTIONS:
                return Query.create()
                        .projectionId(Tables.Transactions.ID)
                        .selection(Tables.Transactions.MODEL_STATE + "=?", String.valueOf(ModelState.DELETED_UNDO.asInt()))
                        .asCursorLoader(getActivity(), TransactionsProvider.uriTransactions());
        }
        return null;
    }

    @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
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

            case LOADER_TAGS:
                item = getResources().getQuantityString(R.plurals.tag, count);
                view = tags_TV;
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
            view.setText(getString(R.string.f_x_y_will_be_deleted, count, item.toLowerCase()));
        }
    }

    @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    @Override public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isAdded()) {
            undoDelete();
        }
    }

    @Override public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        undoDelete();
    }

    @Override protected AlertDialogEvent createEvent(int requestCode, boolean isPositiveClicked) {
        return new DeleteDialogEvent(requestCode, isPositiveClicked, deleteUri, deleteSelection, deleteSelectionArgs);
    }

    @Override protected void onClickPositive() {
        commitDelete();
        super.onClickPositive();
    }

    private void commitDelete() {
        DataStore.commitDelete().selection(deleteSelection, deleteSelectionArgs).from(getActivity(), deleteUri);
    }

    private void undoDelete() {
        DataStore.undoDelete().selection(deleteSelection, deleteSelectionArgs).from(getActivity(), deleteUri);
    }

    public static class DeleteDialogEvent extends AlertDialogEvent {
        private final Uri uri;
        private final String selection;
        private final String[] selectionArgs;

        public DeleteDialogEvent(int requestCode, boolean isPositiveClicked, Uri uri, String selection, String[] selectionArgs) {
            super(requestCode, isPositiveClicked);
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

    public static class Builder extends AlertDialogFragment.Builder {
        public Builder(Context context, int requestCode, Uri deleteUri, String deleteSelection, String... deleteSelectionArgs) {
            super(requestCode);
            super.setTitle(context.getString(R.string.l_delete_confirmation));
            super.setPositiveButtonText(context.getString(R.string.delete));
            super.setNegativeButtonText(context.getString(R.string.cancel));
            super.setPositiveButtonColor(context.getResources().getColor(R.color.text_negative));
            args.putParcelable(ARG_DELETE_URI, deleteUri);
            args.putString(ARG_DELETE_SELECTION, deleteSelection);
            args.putStringArray(ARG_DELETE_SELECTION_ARGS, deleteSelectionArgs);
        }

        @Override public AlertDialogFragment.Builder setTitle(String title) {
            throw new UnsupportedOperationException("setTitle(String) is not supported.");
        }

        @Override public AlertDialogFragment.Builder setMessage(String message) {
            throw new UnsupportedOperationException("setMessage(String) is not supported.");
        }

        @Override public AlertDialogFragment.Builder setPositiveButtonText(String positiveButtonText) {
            throw new UnsupportedOperationException("setPositiveButtonText(String) is not supported.");
        }

        @Override public AlertDialogFragment.Builder setNegativeButtonText(String negativeButtonText) {
            throw new UnsupportedOperationException("setNegativeButtonText(String) is not supported.");
        }

        @Override public AlertDialogFragment.Builder setPositiveButtonColor(int positiveButtonColor) {
            throw new UnsupportedOperationException("setPositiveButtonColor(int) is not supported.");
        }

        @Override public AlertDialogFragment.Builder setNegativeButtonColor(int negativeButtonColor) {
            throw new UnsupportedOperationException("setNegativeButtonColor(int) is not supported.");
        }

        @Override protected BaseDialogFragment createFragment() {
            return new DeleteDialogFragment();
        }
    }
}