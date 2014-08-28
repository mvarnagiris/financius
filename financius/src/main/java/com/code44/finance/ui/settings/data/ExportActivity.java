package com.code44.finance.ui.settings.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.code44.finance.ui.BaseActivity;

public class ExportActivity extends BaseActivity {
    private static final String EXTRA_EXPORT_TYPE = "EXTRA_EXPORT_TYPE";
    private static final String EXTRA_DESTINATION = "EXTRA_DESTINATION";

    public static void start(Context context, ExportType exportType, Destination destination) {
        final Intent intent = makeIntent(context, ExportActivity.class);
        intent.putExtra(EXTRA_EXPORT_TYPE, exportType);
        intent.putExtra(EXTRA_DESTINATION, destination);
        start(context, intent);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Get extras
            final ExportType exportType = (ExportType) getIntent().getSerializableExtra(EXTRA_EXPORT_TYPE);
            final Destination destination = (Destination) getIntent().getSerializableExtra(EXTRA_DESTINATION);

            final BaseExportFragment fragment;
            switch (destination) {
                case File:
                    fragment = FileExportFragment.newInstance(exportType);
                    break;
//                case GoogleDrive:
//                    break;
                default:
                    throw new IllegalArgumentException("Destination " + destination + " is not supported.");
            }

            getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }

    public static enum ExportType {
        Backup, CSV
    }

    public static enum Destination {
        File, GoogleDrive
    }
}
