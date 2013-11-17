package com.code44.finance.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class ProgressDialog extends DialogFragment
{
    private static final String FRAGMENT_PROGRESS = ProgressDialog.class.getName() + "FRAGMENT_PROGRESS";
    // -----------------------------------------------------------------------------------------------------------------
    private static final String ARG_MESSAGE = "ARG_MESSAGE";

    public static void showDialog(FragmentManager fm, String message)
    {
        dismissDialog(fm);

        final Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);

        final ProgressDialog f = new ProgressDialog();
        f.setArguments(args);

        f.show(fm, FRAGMENT_PROGRESS);
    }

    public static void dismissDialog(FragmentManager fm)
    {
        ProgressDialog f = (ProgressDialog) fm.findFragmentByTag(FRAGMENT_PROGRESS);
        if (f != null)
            f.dismiss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Bundle args = getArguments();
        final android.app.ProgressDialog dialog = new android.app.ProgressDialog(getActivity());
        dialog.setMessage(args.getString(ARG_MESSAGE));
        dialog.setIndeterminate(true);
        return dialog;
    }
}