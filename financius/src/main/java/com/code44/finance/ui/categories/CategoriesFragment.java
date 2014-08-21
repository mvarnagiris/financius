package com.code44.finance.ui.categories;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.adapters.CategoriesAdapter;
import com.code44.finance.common.model.CategoryOwner;
import com.code44.finance.common.model.CategoryType;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.model.BaseModel;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.ModelListFragment;

public class CategoriesFragment extends ModelListFragment {
    private static final String ARG_TYPE = "ARG_TYPE";

    private CategoryType type;

    public static CategoriesFragment newInstance(Mode mode, CategoryType type) {
        final Bundle args = makeArgs(mode, null);
        args.putSerializable(ARG_TYPE, type);

        final CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        type = (CategoryType) getArguments().getSerializable(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    protected BaseModelsAdapter createAdapter(Context context) {
        return new CategoriesAdapter(context);
    }

    @Override
    protected CursorLoader getModelsCursorLoader(Context context) {
        final Query query = Tables.Categories.getQuery(type);
        if (getMode() == Mode.VIEW) {
            query.selection(" and " + Tables.Categories.OWNER + "<>?", CategoryOwner.SYSTEM.asString());
        }
        return query.asCursorLoader(context, CategoriesProvider.uriCategories());
    }

    @Override
    protected BaseModel modelFrom(Cursor cursor) {
        return Category.from(cursor);
    }

    @Override
    protected void onModelClick(Context context, View view, int position, String modelServerId, BaseModel model) {
        CategoryActivity.start(context, modelServerId);
    }

    @Override
    protected void startModelEdit(Context context, String modelServerId) {
        CategoryEditActivity.start(context, modelServerId, type);
    }
}
