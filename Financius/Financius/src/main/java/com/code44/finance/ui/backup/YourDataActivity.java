package com.code44.finance.ui.backup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.api.parsers.BackupParser;
import com.code44.finance.ui.BaseActivity;
import com.code44.finance.utils.BackupUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class YourDataActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, YourDataFragment.Callbacks, DriveApi.OnNewContentsCallback, DriveFolder.OnCreateFileCallback
{
    private static final String FRAGMENT_ERROR_DIALOG = "FRAGMENT_ERROR_DIALOG";
    // ---------------------------------------------------------------------------------------------
    private static final int REQUEST_RESOLVE_ERROR = 9000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 9001;
    private static final int REQUEST_FILE = 9002;
    // ---------------------------------------------------------------------------------------------
    private static final DriveId FINANCIUS_FOLDER_ID = DriveId.createFromResourceId("0B2EEtIjPUdX6MERsWlYxN3J6RU0");
    // ---------------------------------------------------------------------------------------------
    private GoogleApiClient client;
    private boolean doBackup = false;
    private boolean doRestore = false;

    public static void start(Context context)
    {
        context.startActivity(new Intent(context, YourDataActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup ActionBar
        setActionBarTitle(R.string.your_data);

        // Setup Drive
        client = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // Add fragment
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, YourDataFragment.newInstance()).commit();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        disconnectClient();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_RESOLVE_ERROR:
                if (resultCode == Activity.RESULT_OK)
                    connectClient();
                return;

            case REQUEST_FILE:
                if (resultCode == RESULT_OK)
                {
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    doRestore = false;
                    new RestoreAsyncTask(client).execute(driveId);
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        performActions();
    }

    @Override
    public void onDisconnected()
    {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        if (!result.hasResolution())
        {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        try
        {
            result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
        }
        catch (IntentSender.SendIntentException e)
        {
            Toast.makeText(this, "Exception while starting resolution activity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestBackup()
    {
        doBackup = true;
        connectClient();
    }

    @Override
    public void onRequestRestore()
    {
        doRestore = true;
        connectClient();
    }

    @Override
    public void onNewContents(DriveApi.ContentsResult result)
    {
        if (!result.getStatus().isSuccess())
        {
            Toast.makeText(App.getAppContext(), "Error while trying to create new file contents", Toast.LENGTH_SHORT).show();
            return;
        }

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle("Financius " + DateUtils.formatDateTime(App.getAppContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR))
                .setMimeType("application/json")
                .setStarred(false).build();

        Drive.DriveApi.getRootFolder(client).createFile(client, changeSet, result.getContents()).addResultCallback(this);
    }

    @Override
    public void onCreateFile(DriveFolder.DriveFileResult result)
    {
        if (!result.getStatus().isSuccess())
        {
            Toast.makeText(App.getAppContext(), "Error while trying to create the file", Toast.LENGTH_SHORT).show();
            return;
        }

        doBackup = false;
        new BackupAsyncTask(client).execute(result.getDriveFile());
    }

    private void connectClient()
    {
        if (client.isConnecting())
            return;

        if (client.isConnected())
        {
            onConnected(null);
            return;
        }

        if (checkGooglePlayServicesAvailable())
            client.connect();
    }

    private void disconnectClient()
    {
        if (client.isConnected() || client.isConnecting())
            client.disconnect();
    }

    private boolean checkGooglePlayServicesAvailable()
    {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, REQUEST_GOOGLE_PLAY_SERVICES).show();
            else
                Toast.makeText(App.getAppContext(), "Device is not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void performActions()
    {
        if (doBackup)
        {
            Drive.DriveApi.newContents(client).addResultCallback(this);
        }

        if (doRestore)
        {
            IntentSender intentSender = Drive.DriveApi
                    .newOpenFileActivityBuilder()
                    .setMimeType(new String[]{"application/json"})
                    .build(client);
            try
            {
                startIntentSenderForResult(intentSender, REQUEST_FILE, null, 0, 0, 0);
            }
            catch (IntentSender.SendIntentException e)
            {
                Toast.makeText(App.getAppContext(), "Unable to send intent", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class BackupAsyncTask extends AsyncTask<DriveFile, Void, Boolean>
    {
        private final GoogleApiClient client;

        public BackupAsyncTask(GoogleApiClient client)
        {
            this.client = client;
        }

        @Override
        protected Boolean doInBackground(DriveFile... args)
        {
            DriveFile file = args[0];
            try
            {
                DriveApi.ContentsResult contentsResult = file.openContents(client, DriveFile.MODE_WRITE_ONLY, null).await();
                if (!contentsResult.getStatus().isSuccess())
                {
                    return false;
                }
                OutputStream outputStream = contentsResult.getContents().getOutputStream();
                outputStream.write(BackupUtils.generateBackupJson().toString().getBytes());
                com.google.android.gms.common.api.Status status = file.commitAndCloseContents(client, contentsResult.getContents()).await();
                return status.getStatus().isSuccess();
            }
            catch (IOException e)
            {
                Toast.makeText(App.getAppContext(), "IOException while appending to the output stream", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if (!result)
            {
                Toast.makeText(App.getAppContext(), "Error while creating backup", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(App.getAppContext(), "Successfully created backup", Toast.LENGTH_SHORT).show();
        }
    }

    private class RestoreAsyncTask extends AsyncTask<DriveId, Boolean, Exception>
    {
        private final GoogleApiClient client;

        private RestoreAsyncTask(GoogleApiClient client)
        {
            this.client = client;
        }

        @Override
        protected Exception doInBackground(DriveId... params)
        {
            String contents;
            DriveFile file = Drive.DriveApi.getFile(client, params[0]);

            DriveApi.ContentsResult contentsResult = file.openContents(client, DriveFile.MODE_READ_ONLY, null).await();
            if (!contentsResult.getStatus().isSuccess())
            {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(contentsResult.getContents().getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    builder.append(line);
                }
                contents = builder.toString();
            }
            catch (IOException e)
            {
                return e;
            }

            file.discardContents(client, contentsResult.getContents()).await();

            try
            {
                new BackupParser().parse(new Gson().fromJson(contents, JsonElement.class).getAsJsonObject(), null);
            }
            catch (Exception e)
            {
                return e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Exception e)
        {
            super.onPostExecute(e);

            if (e != null)
            {
                Toast.makeText(App.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            else
            {
                Toast.makeText(App.getAppContext(), "Restored successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}