package com.code44.finance.ui.dialogs;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import com.code44.finance.R;
import com.code44.finance.adapters.ColorsAdapter;

public class ColorSelectDialog extends DialogFragment implements AdapterView.OnItemClickListener
{
    private GridView grid_V;
    // -----------------------------------------------------------------------------------------------------------------
    private DialogCallbacks listener;

    public static ColorSelectDialog newInstance(DialogCallbacks listener)
    {
        ColorSelectDialog f = new ColorSelectDialog();
        f.setListener(listener);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_color_select, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        grid_V = (GridView) view.findViewById(R.id.grid_V);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        TypedArray ta = getResources().obtainTypedArray(R.array.category_colors);
        int[] colors = new int[ta.length()];
        for (int i = 0; i < ta.length(); i++)
        {
            colors[i] = ta.getColor(i, 0);
        }
        ta.recycle();
        final ColorsAdapter adapter = new ColorsAdapter(getActivity(), colors);
        grid_V.setAdapter(adapter);
        grid_V.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (listener != null)
            listener.onColorSelected((Integer) grid_V.getAdapter().getItem(position));
        dismiss();
    }

    public void setListener(DialogCallbacks listener)
    {
        this.listener = listener;
    }

    public static interface DialogCallbacks
    {
        public void onColorSelected(int color);
    }
}