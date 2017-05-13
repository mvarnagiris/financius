package com.code44.finance.ui.common.fragments;

import com.code44.finance.ui.common.navigation.NavigationFragment;
import com.code44.finance.ui.dialogs.ColorDialogFragment;
import com.code44.finance.ui.dialogs.DatePickerDialog;
import com.code44.finance.ui.dialogs.DeleteDialogFragment;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.code44.finance.ui.dialogs.TimePickerDialog;
import com.code44.finance.ui.playservices.GoogleApiFragment;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {NavigationFragment.class, DeleteDialogFragment.class, ListDialogFragment.class, GoogleApiFragment.class,
                   DatePickerDialog.class, TimePickerDialog.class, ColorDialogFragment.class,})
public class FragmentsModule {
}
