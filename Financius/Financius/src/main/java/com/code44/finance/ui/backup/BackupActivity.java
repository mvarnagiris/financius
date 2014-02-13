package com.code44.finance.ui.backup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.code44.finance.R;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.user.DriveFragment;

/**
 * Created by Mantas on 09/06/13.
 */
public class BackupActivity extends BaseActivity// implements BackupFragment.Callbacks
{
    private static final String FRAGMENT_BACKUP = "FRAGMENT_BACKUP";
    private static final String FRAGMENT_DRIVE = "FRAGMENT_DRIVE";
    private BackupFragment backup_F;
    private DriveFragment drive_F;

    public static void startBackup(Context context)
    {
        Intent intent = new Intent(context, BackupActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setActionBarTitle(R.string.backup);

        // Add drive fragment
        drive_F = (DriveFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_DRIVE);
        if (drive_F == null)
        {
            drive_F = DriveFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(drive_F, FRAGMENT_DRIVE).commit();
        }

        // Add backup fragment
        backup_F = (BackupFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_BACKUP);
        if (backup_F == null)
        {
            backup_F = BackupFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, backup_F, FRAGMENT_BACKUP).commit();
        }

        // Setup listener
        drive_F.setCallbacksListener(backup_F);
    }

//    @Override
//    public GoogleUserFragment.GoogleRequest onGetWorkingGoogleRequest()
//    {
//        return drive_F != null ? drive_F.getWorkingRequest() : null;
//    }
//
//    @Override
//    public boolean onRequestFiles()
//    {
//        if (drive_F != null)
//        {
//            drive_F.getFiles();
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean onStoreFile(DriveFragment.FileGenerator fileGenerator, String fileToOverwriteId)
//    {
//        if (drive_F != null)
//        {
//            drive_F.storeFile(fileGenerator, fileToOverwriteId);
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean onGetFileContents(FileHandler fileHandler, String fileId)
//    {
//        if (drive_F != null)
//        {
//            drive_F.getFileContents(fileHandler, fileId);
//            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean onDeleteFile(String fileId)
//    {
//        if (drive_F != null)
//        {
//            drive_F.deleteFile(fileId);
//            return true;
//        }
//
//        return false;
//    }
}