package com.code44.finance.views.cards;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.code44.finance.R;

@SuppressWarnings("UnusedDeclaration")
public class EditTextCardView extends CardViewV2
{
    private final EditText text_ET;

    public EditTextCardView(Context context)
    {
        this(context, null);
    }

    public EditTextCardView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public EditTextCardView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Setup
        //noinspection ConstantConditions
        int padding = getResources().getDimensionPixelSize(R.dimen.space_normal);
        container_V.setPadding(padding, container_V.getPaddingTop(), padding, container_V.getPaddingBottom());
        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!text_ET.hasFocus())
                    text_ET.requestFocus();
                showKeyboard();
            }
        });
        setMinimumHeight(getResources().getDimensionPixelSize(R.dimen.recommended_touch_size));
        text_ET = new EditText(context);
        text_ET.setBackgroundColor(0);
        text_ET.setPadding(0, 0, 0, 0);
        text_ET.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        //noinspection ConstantConditions
        text_ET.setTextColor(getResources().getColor(R.color.text_primary));
        text_ET.setHintTextColor(getResources().getColor(R.color.text_secondary));
        text_ET.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_large));
        setContentView(text_ET);

        //if (isInEditMode())
        setHint(getResources().getString(R.string.note));
    }

    public void setText(CharSequence text)
    {
        text_ET.setText(text);
    }

    public void setHint(CharSequence hint)
    {
        text_ET.setHint(hint);
    }

    private void showKeyboard()
    {
        text_ET.requestFocus();
        //noinspection ConstantConditions
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(text_ET, InputMethodManager.SHOW_IMPLICIT);
    }
}
