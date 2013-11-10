package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.code44.finance.R;
import com.code44.finance.db.Tables;
import com.code44.finance.providers.CategoriesProvider;
import com.code44.finance.ui.ItemEditFragment;
import com.code44.finance.ui.dialogs.ColorSelectDialog;
import com.code44.finance.utils.AnimUtils;

public class CategoryDetailsFragment extends ItemEditFragment implements View.OnClickListener, ColorSelectDialog.DialogCallbacks
{
    private static final String ARG_PARENT_ID = "ARG_PARENT_ID";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String STATE_PARENT_ID = "STATE_PARENT_ID";
    private static final String STATE_TITLE = "STATE_TITLE";
    private static final String STATE_COLOR = "STATE_COLOR";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String FRAGMENT_SELECT_COLOR = "FRAGMENT_SELECT_COLOR";
    // -----------------------------------------------------------------------------------------------------------------
    private static final int LOADER_PARENT = 1;
    // -----------------------------------------------------------------------------------------------------------------
    private View mainContainer_V;
    private View color_V;
    private TextView mainTitle_TV;
    private Button color_B;
    private EditText title_ET;
    // -----------------------------------------------------------------------------------------------------------------
    private long parentId;
    private int color;
    private int level;

    public static CategoryDetailsFragment newInstance(long itemId, long parentId)
    {
        final Bundle args = makeArgs(itemId);
        args.putLong(ARG_PARENT_ID, parentId);

        final CategoryDetailsFragment f = new CategoryDetailsFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_category_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        mainContainer_V = view.findViewById(R.id.mainContainer_V);
        color_V = view.findViewById(R.id.color_V);
        mainTitle_TV = (TextView) view.findViewById(R.id.mainTitle_TV);
        color_B = (Button) view.findViewById(R.id.color_B);
        title_ET = (EditText) view.findViewById(R.id.title_ET);

        // Setup
        color_B.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Restore color dialog fragment
        final ColorSelectDialog selectColor_F = (ColorSelectDialog) getFragmentManager().findFragmentByTag(FRAGMENT_SELECT_COLOR);
        if (selectColor_F != null)
            selectColor_F.setListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putLong(STATE_PARENT_ID, getParentId());
        outState.putString(STATE_TITLE, getTitle(false));
        outState.putInt(STATE_COLOR, getColor(false));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle)
    {
        Uri uri;
        String[] projection;
        String selection;
        String[] selectionArgs;
        String sortOrder;

        switch (id)
        {
            case LOADER_PARENT:
            {
                uri = CategoriesProvider.uriCategory(getActivity(), parentId);
                projection = null;
                selection = null;
                selectionArgs = null;
                sortOrder = null;
                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
            }
        }

        return super.onCreateLoader(id, bundle);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        switch (cursorLoader.getId())
        {
            case LOADER_PARENT:
                if (cursor != null && cursor.moveToFirst())
                {
                    setParent(
                            cursor.getLong(cursor.getColumnIndex(Tables.Categories.ID)),
                            cursor.getString(cursor.getColumnIndex(Tables.Categories.TITLE)),
                            cursor.getInt(cursor.getColumnIndex(Tables.Categories.LEVEL)),
                            cursor.getInt(cursor.getColumnIndex(Tables.Categories.COLOR)));
                }
                break;
        }
        super.onLoadFinished(cursorLoader, cursor);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.color_B:
                ColorSelectDialog.newInstance(this).show(getFragmentManager(), FRAGMENT_SELECT_COLOR);
                break;
        }
    }

    public long getParentId()
    {
        return parentId;
    }

    public String getTitle(boolean check)
    {
        if (check && TextUtils.isEmpty(title_ET.getText()))
            AnimUtils.shake(title_ET);

        //noinspection ConstantConditions
        return title_ET.getText().toString();
    }

    @Override
    public void onColorSelected(int color)
    {
        setColor(color);
    }

    public int getColor(boolean check)
    {
        if (check && color == 0)
            AnimUtils.shake(color_B);
        return color;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    @Override
    protected Loader<Cursor> createItemLoader(Context context, long itemId)
    {
        Uri uri = CategoriesProvider.uriCategory(context, itemId);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        return new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected boolean bindItem(Cursor c, boolean isDataLoaded)
    {
        if (!isDataLoaded && c != null && c.moveToFirst())
        {
            onGotParentId(c.getLong(c.getColumnIndex(Tables.Categories.PARENT_ID)));
            setTitle(c.getString(c.getColumnIndex(Tables.Categories.TITLE)));
            setColor(c.getInt(c.getColumnIndex(Tables.Categories.COLOR)));
            return true;
        }
        return isDataLoaded;
    }

    @Override
    protected void restoreOrInit(long itemId, Bundle savedInstanceState)
    {
        if (savedInstanceState != null)
        {
            onGotParentId(savedInstanceState.getLong(STATE_PARENT_ID));
            setTitle(savedInstanceState.getString(STATE_TITLE));
            setColor(savedInstanceState.getInt(STATE_COLOR));
        }
        else if (itemId == 0)
        {
            setColor(getResources().getColor(R.color.f_light_darker5));
            // Load parent data if this is "new" item
            final long parentId = getArguments().getLong(ARG_PARENT_ID, 0);
            if (parentId > 0)
                onGotParentId(parentId);
        }
    }

    @Override
    protected boolean onSave(Context context, long itemId)
    {
        // Ignore
        return false;
    }

    @Override
    protected boolean onDiscard()
    {
        // Ignore
        return false;
    }

    private void setTitle(String title)
    {
        title_ET.setText(title);
    }

    private void setColor(int color)
    {
        this.color = color;
        color_B.setTextColor(color);
        SpannableStringBuilder ssb = new SpannableStringBuilder("        ");
        ssb.setSpan(new BackgroundColorSpan(color), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        color_B.setText(ssb);
        color_V.setBackgroundColor(color);
    }

    private void onGotParentId(long parentId)
    {
        this.parentId = parentId;
        getLoaderManager().restartLoader(LOADER_PARENT, null, this);
    }

    private void setParent(long id, String title, int level, int color)
    {
        this.parentId = id;
        mainTitle_TV.setText(title);
        setLevel(level + 1);

        if (color != 0 && (level == 1 || (getColor(false) == 0 && itemId > 0)))
            setColor(color);

        if (level == 0)
        {
            // This is main category. Prepare UI.
            mainContainer_V.setVisibility(View.GONE);
            color_B.setVisibility(View.VISIBLE);
        }
        else
        {
            // This is sub category. Prepare UI.
            mainContainer_V.setVisibility(View.VISIBLE);
            color_B.setVisibility(View.GONE);
        }
    }
}