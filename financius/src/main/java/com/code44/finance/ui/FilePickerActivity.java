package com.code44.finance.ui;

import android.app.Fragment;
import android.content.Intent;

public class FilePickerActivity extends com.nononsenseapps.filepicker.FilePickerActivity {
    public static void startFile(Fragment fragment, int requestCode) {
        final Intent intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
        intent.putExtra(com.nononsenseapps.filepicker.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        intent.putExtra(com.nononsenseapps.filepicker.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(com.nononsenseapps.filepicker.FilePickerActivity.EXTRA_MODE, MODE_FILE);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startDir(Fragment fragment, int requestCode) {
        final Intent intent = new Intent(fragment.getActivity(), FilePickerActivity.class);
        intent.putExtra(com.nononsenseapps.filepicker.FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        intent.putExtra(com.nononsenseapps.filepicker.FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(com.nononsenseapps.filepicker.FilePickerActivity.EXTRA_MODE, MODE_DIR);
        fragment.startActivityForResult(intent, requestCode);
    }
}
