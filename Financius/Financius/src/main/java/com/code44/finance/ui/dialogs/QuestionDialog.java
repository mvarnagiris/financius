package com.code44.finance.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import com.code44.finance.R;

public class QuestionDialog extends DialogFragment
{
    private static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_MESSAGE = "ARG_MESSAGE";
    private static final String ARG_TAG = "ARG_TAG";
    // -----------------------------------------------------------------------------------------------------------------
    private DialogCallbacks listener;

    public static QuestionDialog newInstance(DialogCallbacks listener, int requestCode, String title, String message, String tag)
    {
        final Bundle args = new Bundle();
        args.putInt(ARG_REQUEST_CODE, requestCode);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TAG, tag);

        final QuestionDialog f = new QuestionDialog();
        f.setArguments(args);
        f.setListener(listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Bundle args = getArguments();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(args.getString(ARG_TITLE)).setMessage(args.getString(ARG_MESSAGE)).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (listener != null)
                    listener.onQuestionYes(args.getInt(ARG_REQUEST_CODE), args.getString(ARG_TAG));
            }
        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (listener != null)
                    listener.onQuestionNo(args.getInt(ARG_REQUEST_CODE), args.getString(ARG_TAG));
            }
        });

        return builder.create();
    }

    public void setListener(DialogCallbacks listener)
    {
        this.listener = listener;
    }

    public static interface DialogCallbacks
    {
        public void onQuestionYes(int requestCode, String tag);

        public void onQuestionNo(int requestCode, String tag);
    }
}