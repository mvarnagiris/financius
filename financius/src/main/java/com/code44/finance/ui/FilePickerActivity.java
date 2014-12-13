package com.code44.finance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.code44.finance.common.utils.Strings;

import java.io.File;

public class FilePickerActivity extends com.nononsenseapps.filepicker.FilePickerActivity {
    public static void startFile(Activity activity, int requestCode, String startPath) {
        final Intent intent = makeIntent(activity, false, MODE_FILE, startPath);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startDir(Activity activity, int requestCode, String startPath) {
        final Intent intent = makeIntent(activity, true, MODE_DIR, startPath);
        activity.startActivityForResult(intent, requestCode);
    }

    private static Intent makeIntent(Context context, boolean allowCreateDir, int mode, String startPath) {
        final Intent intent = new Intent(context, FilePickerActivity.class);
        intent.putExtra(EXTRA_ALLOW_CREATE_DIR, allowCreateDir);
        intent.putExtra(EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(EXTRA_MODE, mode);
        if (!Strings.isEmpty(startPath) && new File(startPath).exists()) {
            intent.putExtra(EXTRA_START_PATH, startPath);
        }
        return intent;
    }
}
