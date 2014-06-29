package com.code44.finance.ui.categories;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code44.finance.R;
import com.code44.finance.adapters.BaseModelsAdapter;
import com.code44.finance.adapters.CategoriesAdapter;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.Tables;
import com.code44.finance.data.db.model.BaseModel;
import com.code44.finance.data.db.model.Category;
import com.code44.finance.data.providers.CategoriesProvider;
import com.code44.finance.ui.ModelListActivity;
import com.code44.finance.ui.ModelListFragment;

public class CategoriesFragment extends ModelListFragment {
    private static final String ARG_TYPE = "ARG_TYPE";

    private Category.Type type;

    public static CategoriesFragment newInstance(ModelListActivity.Mode mode, Category.Type type) {
        final Bundle args = makeArgs(mode);
        args.putSerializable(ARG_TYPE, type);

        final CategoriesFragment fragment = new CategoriesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get arguments
        type = (Category.Type) getArguments().getSerializable(ARG_TYPE);
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
    protected Uri getUri() {
        return CategoriesProvider.uriCategories();
    }

    @Override
    protected Query getQuery() {
        return Query.get()
                .projectionId(Tables.Categories.ID)
                .projection(Tables.Categories.PROJECTION)
                .selection(Tables.Categories.TYPE + "=?", String.valueOf(type.asInt()));
    }

    @Override
    protected BaseModel modelFrom(Cursor cursor) {
        return Category.from(cursor);
    }
}
