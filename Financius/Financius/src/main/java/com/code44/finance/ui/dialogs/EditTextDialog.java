package com.code44.finance.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import com.code44.finance.R;

public class EditTextDialog extends DialogFragment
{
    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_TEXT = "ARG_TEXT";
    private EditText text_ET;
    private EditTextDialogListener listener;

    public static EditTextDialog newInstance(EditTextDialogListener listener, int requestCode, String title, String text)
    {
        EditTextDialog f = new EditTextDialog();

        final Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TEXT, text);
        f.setArguments(args);
        f.listener = listener;

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Get arguments
        Bundle args = getArguments();

        final int requestCode = args.getInt(ARG_REQUEST_CODE, 0);
        text_ET = new EditText(getActivity());
        text_ET.setText(savedInstanceState != null ? savedInstanceState.getString(ARG_TEXT) : args.getString(ARG_TEXT));
        text_ET.setSelection(text_ET.getText().length());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(args.getString(ARG_TITLE)).setView(text_ET).setPositiveButton(R.string.done, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (listener != null)
                    listener.onTextEntered(requestCode, text_ET.getText().toString());
            }
        }).setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_TEXT, text_ET.getText().toString());
    }

    public void setListener(EditTextDialogListener listener)
    {
        this.listener = listener;
    }

    public static interface EditTextDialogListener
    {
        public void onTextEntered(int requestCode, String text);
    }
}
