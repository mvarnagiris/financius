package com.code44.finance.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.code44.finance.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class SelectColorFragment extends DialogFragment {
    private static final String ARG_COLOR = "ARG_COLOR";

    private OnColorSelectedListener mOnColorSelectedListener;

    public static void show(FragmentManager fm, String tag, int color, OnColorSelectedListener listener) {
        final Bundle args = new Bundle();
        args.putInt(ARG_COLOR, color);

        final SelectColorFragment fragment = new SelectColorFragment();
        fragment.setArguments(args);
        fragment.setOnColorSelectedListener(listener);
        fragment.show(fm, tag);
    }

    public static void setListenerIfVisible(FragmentManager fm, String tag, OnColorSelectedListener listener) {
        final SelectColorFragment fragment = (SelectColorFragment) fm.findFragmentByTag(tag);
        if (fragment != null) {
            fragment.setOnColorSelectedListener(listener);
        }
    }

    public static void removeListenerIfVisible(FragmentManager fm, String tag) {
        setListenerIfVisible(fm, tag, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int style = DialogFragment.STYLE_NO_FRAME;
        final int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_color, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get arguments
        final int color = getArguments().getInt(ARG_COLOR);

        // Get views
        final ColorPicker picker_CP = (ColorPicker) view.findViewById(R.id.picker_CP);
        final SaturationBar saturation_SB = (SaturationBar) view.findViewById(R.id.saturation_SB);
        final ValueBar value_VB = (ValueBar) view.findViewById(R.id.value_SB);
        final Button cancel_B = (Button) view.findViewById(R.id.cancel_B);
        final Button select_B = (Button) view.findViewById(R.id.select_B);

        // Setup
        picker_CP.addSaturationBar(saturation_SB);
        picker_CP.addValueBar(value_VB);
        picker_CP.setColor(color);
        picker_CP.setOldCenterColor(color);
        picker_CP.setNewCenterColor(color);
        cancel_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        select_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnColorSelectedListener != null) {
                    mOnColorSelectedListener.onColorSelected(picker_CP.getColor());
                }
                dismiss();
            }
        });
    }

    public void setOnColorSelectedListener(OnColorSelectedListener onColorSelectedListener) {
        this.mOnColorSelectedListener = onColorSelectedListener;
    }

    public static interface OnColorSelectedListener {
        public void onColorSelected(int color);
    }
}
