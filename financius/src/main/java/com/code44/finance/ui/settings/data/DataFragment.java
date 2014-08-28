package com.code44.finance.ui.settings.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_BACKUP_DESTINATION = 1;

    private static final String FRAGMENT_DESTINATION = "FRAGMENT_DESTINATION";

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final Button backup_B = (Button) view.findViewById(R.id.backup_B);

        // Setup
        backup_B.setOnClickListener(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backup_B:
                chooseBackup();
                break;
        }
    }

    @Subscribe public void onDestinationSelected(ListDialogFragment.ListDialogEvent event) {
        switch (event.getRequestCode()) {
            case REQUEST_BACKUP_DESTINATION:
                if (event.getPosition() == 0) {
                    backupGoogleDrive();
                } else if (event.getPosition() == 1) {
                    backupFile();
                }
                break;
        }
        event.dismiss();
    }

    private void chooseBackup() {
        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.ListDialogItem(getString(R.string.google_drive)));
        items.add(new ListDialogFragment.ListDialogItem(getString(R.string.file)));

        ListDialogFragment.build(REQUEST_BACKUP_DESTINATION)
                .setTitle(getString(R.string.backup))
                .setNegativeButtonText(getString(R.string.cancel))
                .setItems(items)
                .build()
                .show(getChildFragmentManager(), FRAGMENT_DESTINATION);
    }

    private void backupFile() {

    }

    private void backupGoogleDrive() {

    }
}
