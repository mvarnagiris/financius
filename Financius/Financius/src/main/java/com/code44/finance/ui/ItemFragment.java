package com.code44.finance.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.code44.finance.R;
import com.code44.finance.ui.dialogs.QuestionDialog;

public abstract class ItemFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, QuestionDialog.DialogCallbacks
{
    public static final String ARG_ITEM_ID = ItemFragment.class.getName() + ".ARG_ITEM_ID";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final String FRAGMENT_DELETE_DIALOG = ItemActivity.class.getName() + ".FRAGMENT_DELETE_DIALOG";
    // -----------------------------------------------------------------------------------------------------------------
    protected static final int REQUEST_DELETE = 8981;
    // -----------------------------------------------------------------------------------------------------------------
    protected static final int LOADER_ITEM = 9123;
    // -----------------------------------------------------------------------------------------------------------------
    protected long itemId;

    /**
     * Use this to add all required arguments
     *
     * @param itemId Id of the item.
     * @return Bundle with all required arguments.
     */
    protected static Bundle makeArgs(long itemId)
    {
        final Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemId);
        return args;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // Get arguments. We also check savedInstanceState, because Id might change in multi pane layout, and same fragment reused.
        final Bundle args = getArguments();
        itemId = args.getLong(ARG_ITEM_ID);

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Restore delete dialog fragment
        final QuestionDialog deleteConfirmation_F = (QuestionDialog) getFragmentManager().findFragmentByTag(FRAGMENT_DELETE_DIALOG);
        if (deleteConfirmation_F != null)
            deleteConfirmation_F.setListener(this);

        // Loader
        if (itemId > 0)
            initLoaders();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.item, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_edit:
                startItemEdit(getActivity(), itemId, item.getActionView());
                break;

            case R.id.action_delete:
                QuestionDialog.newInstance(this, REQUEST_DELETE, getString(R.string.delete), getString(R.string.l_delete_confirmation), null).show(getFragmentManager(), FRAGMENT_DELETE_DIALOG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        switch (id)
        {
            case LOADER_ITEM:
                return createItemLoader(getActivity(), itemId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_ITEM:
                bindItem(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
    }

    /**
     * Start item edit here.
     *
     * @param itemId This is required by {@link com.code44.finance.ui.ItemEditActivity}.
     * @param expandFrom
     */
    protected abstract void startItemEdit(Context context, long itemId, View expandFrom);

    /**
     * Delete the item here.
     *
     * @param itemIds For convenience.
     * @return {@code true} if delete was successful; {@code false otherwise}.
     */
    protected abstract boolean onDeleteItem(Context context, long[] itemIds);

    protected abstract Loader<Cursor> createItemLoader(Context context, long itemId);

    /**
     * Called when loader gets item from database.
     *
     * @param c Cursor.
     */
    protected abstract void bindItem(Cursor c);

    @Override
    public void onQuestionYes(int requestCode, String tag)
    {
        if (onDeleteItem(getActivity(), new long[]{itemId}))
            getActivity().finish();
    }

    @Override
    public void onQuestionNo(int requestCode, String tag)
    {
    }

    protected void initLoaders()
    {
        getLoaderManager().initLoader(LOADER_ITEM, null, this);
    }
}