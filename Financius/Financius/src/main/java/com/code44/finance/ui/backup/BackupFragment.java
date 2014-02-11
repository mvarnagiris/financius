package com.code44.finance.ui.backup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import com.code44.finance.R;
import com.code44.finance.adapters.BackupFilesAdapter;
import com.code44.finance.parsers.BackupParser;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.ui.ItemActivity;
import com.code44.finance.ui.dialogs.EditTextDialog;
import com.code44.finance.ui.dialogs.QuestionDialog;
import com.code44.finance.user.AppUser;
import com.code44.finance.user.DriveFragment;
import com.code44.finance.user.GoogleUserFragment;
import com.code44.finance.utils.BackupUtils;
import com.google.api.services.drive.model.File;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Mantas on 09/06/13.
 */
public class BackupFragment extends BaseFragment implements DriveFragment.Callbacks, View.OnClickListener, EditTextDialog.EditTextDialogListener, AdapterView.OnItemClickListener, QuestionDialog.DialogCallbacks
{
    private static final String FRAGMENT_DEVICE_NAME = "FRAGMENT_DEVICE_NAME";
    private static final String FRAGMENT_DELETE_DIALOG = ItemActivity.class.getName() + ".FRAGMENT_DELETE_DIALOG";
    private static final String FRAGMENT_RESTORE_DIALOG = ItemActivity.class.getName() + ".FRAGMENT_DELETE_DIALOG";
    // -----------------------------------------------------------------------------------------------------------------
    private static final int REQUEST_DEVICE_NAME = 1;
    private static final int REQUEST_RESTORE = 2;
    private static final int REQUEST_DELETE = 3;
    // -----------------------------------------------------------------------------------------------------------------
    private final View.OnClickListener overflowClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final String fileId = (String) view.getTag();
            final ListPopupWindow popup = new ListPopupWindow(getActivity());
            popup.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[]{getString(R.string.restore), getString(R.string.delete)}));
            popup.setContentWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getActivity().getResources().getDisplayMetrics()));
            popup.setAnchorView(view);
            popup.setModal(true);
            popup.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
                {
                    popup.dismiss();
                    if (callbacks != null)
                    {
                        if (position == 0)
                            QuestionDialog.newInstance(BackupFragment.this, REQUEST_RESTORE, getString(R.string.restore), getString(R.string.l_backup_restore), fileId).show(getFragmentManager(), FRAGMENT_RESTORE_DIALOG);
                        else
                            QuestionDialog.newInstance(BackupFragment.this, REQUEST_DELETE, getString(R.string.delete), getString(R.string.l_backup_delete), fileId).show(getFragmentManager(), FRAGMENT_DELETE_DIALOG);
                    }
                }
            });
            popup.show();
        }
    };
    private View buttonBar_V;
    private View separator_V;
    private View notEnabledContainer_V;
    private TextView deviceName_TV;
    private ListView list_V;
    private ProgressBar driveProgress_PB;
    private BackupFilesAdapter adapter;
    private Callbacks callbacks;
    private boolean isDriveWorking;

    public static BackupFragment newInstance()
    {
        BackupFragment f = new BackupFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        if (activity instanceof Callbacks)
            callbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_backup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        buttonBar_V = view.findViewById(R.id.buttonBar_V);
        separator_V = view.findViewById(R.id.separator_V);
        notEnabledContainer_V = view.findViewById(R.id.notEnabledContainer_V);
        deviceName_TV = (TextView) view.findViewById(R.id.deviceName_TV);
        list_V = (ListView) view.findViewById(R.id.list_V);
        driveProgress_PB = (ProgressBar) view.findViewById(R.id.driveProgress_PB);
        final Button driveBackup_B = (Button) view.findViewById(R.id.driveBackup_B);
        final TextView enable_TV = (TextView) view.findViewById(R.id.enable_TV);

        // Setup
        enable_TV.setText(Html.fromHtml(getString(R.string.f_enable_backup, getString(R.string.enable_backup).toUpperCase())));

        // Set OnClickListener
        view.findViewById(R.id.enable_FL).setOnClickListener(this);
        view.findViewById(R.id.deviceName_B).setOnClickListener(this);
        driveBackup_B.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // Setup
        adapter = new BackupFilesAdapter(getActivity(), overflowClickListener);
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);
        deviceName_TV.setText(AppUser.getDefault(getActivity()).getDriveDeviceName());

        // Restore delete dialog fragment
        QuestionDialog fragment = (QuestionDialog) getFragmentManager().findFragmentByTag(FRAGMENT_DELETE_DIALOG);
        if (fragment != null)
            fragment.setListener(this);

        // Restore restore dialog fragment
        fragment = (QuestionDialog) getFragmentManager().findFragmentByTag(FRAGMENT_RESTORE_DIALOG);
        if (fragment != null)
            fragment.setListener(this);

        // Restore device name dialog fragment
        EditTextDialog fragmentEditText = (EditTextDialog) getFragmentManager().findFragmentByTag(FRAGMENT_DEVICE_NAME);
        if (fragmentEditText != null)
            fragmentEditText.setListener(this);

            // Init
        isDriveWorking = false;
        final GoogleUserFragment.GoogleRequest workingRequest = getWorkingRequest();
        if (workingRequest != null)
        {
            // There is GoogleRequest already in progress
            if (workingRequest instanceof DriveFragment.GetFilesRequest || workingRequest instanceof DriveFragment.StoreFileRequest || workingRequest instanceof DriveFragment.DeleteFileRequest || workingRequest instanceof DriveFragment.GetFileContentsRequest)
                isDriveWorking = true;
        }
        else
        {
            // No GoogleRequests are working
            if (AppUser.getDefault(getActivity()).isDriveBackupEnabled() && callbacks != null)
            {
                // Drive backup is enabled, ask for files list
                callbacks.onRequestFiles();
            }
        }
        updateViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.backup, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.action_disableBackup).setVisible(AppUser.getDefault(getActivity()).isDriveBackupEnabled());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_disableBackup:
                AppUser.getDefault(getActivity()).setDriveBackupEnabled(false);
                getActivity().supportInvalidateOptionsMenu();
                updateViews();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.enable_FL:
                if (callbacks != null)
                    callbacks.onRequestFiles();
                break;

            case R.id.driveBackup_B:
                if (callbacks != null && !isDriveWorking)
                    callbacks.onStoreFile(new BackupFileGenerator(getActivity(), deviceName_TV.getText().toString()), null);
                break;

            case R.id.deviceName_B:
                EditTextDialog.newInstance(this, REQUEST_DEVICE_NAME, getString(R.string.device_name), deviceName_TV.getText().toString()).show(getFragmentManager(), FRAGMENT_DEVICE_NAME);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        if (callbacks != null)
            QuestionDialog.newInstance(BackupFragment.this, REQUEST_RESTORE, getString(R.string.restore), getString(R.string.l_backup_restore), ((File) adapter.getItem(position)).getId()).show(getFragmentManager(), FRAGMENT_RESTORE_DIALOG);
    }

    @Override
    public void onRequestPending(GoogleUserFragment.GoogleRequest request)
    {
        if (request instanceof DriveFragment.GetFilesRequest || request instanceof DriveFragment.StoreFileRequest || request instanceof DriveFragment.DeleteFileRequest || request instanceof DriveFragment.GetFileContentsRequest)
            isDriveWorking = true;
        updateViews();
    }

    @Override
    public void onRequestStarted(GoogleUserFragment.GoogleRequest request)
    {

    }

    @Override
    public void onRequestSucceeded(GoogleUserFragment.GoogleResult result)
    {
        if (result instanceof DriveFragment.GetFilesResult)
        {
            isDriveWorking = false;

            // Set Drive backup as enabled
            final AppUser user = AppUser.getDefault(getActivity());
            if (!user.isDriveBackupEnabled())
            {
                user.setDriveBackupEnabled(true);
                getActivity().supportInvalidateOptionsMenu();
            }

            // Set files list
            adapter.setList(((DriveFragment.GetFilesResult) result).getFiles());
        }
        else if (result instanceof DriveFragment.StoreFileResult)
        {
            isDriveWorking = false;
            final File backupFile = ((DriveFragment.StoreFileResult) result).getFile();
            AppUser.getDefault(getActivity()).setDriveDeviceName(backupFile.getDescription());
            adapter.addItem(backupFile);
        }
        else if (result.getRequest() instanceof DriveFragment.DeleteFileRequest)
        {
            isDriveWorking = false;
            if (callbacks != null)
                callbacks.onRequestFiles();
        }
        else if (result.getRequest() instanceof DriveFragment.GetFileContentsRequest)
        {
            isDriveWorking = false;
        }

        updateViews();
    }

    @Override
    public void onRequestFailed(GoogleUserFragment.GoogleRequest request, Exception e)
    {
        if (request instanceof DriveFragment.GetFilesRequest || request instanceof DriveFragment.StoreFileRequest || request instanceof DriveFragment.DeleteFileRequest || request instanceof DriveFragment.GetFileContentsRequest)
            isDriveWorking = false;
        updateViews();

        // TODO Show something in UI when request fails
        // TODO Use resource
//        backupDate_TV.setText("Request failed - " + e.getMessage());
    }

    @Override
    public void onAuthorizationFailed()
    {
        isDriveWorking = false;
        AppUser.getDefault(getActivity()).setDriveBackupEnabled(false);
        getActivity().supportInvalidateOptionsMenu();
        updateViews();
    }

    @Override
    public void onTextEntered(int requestCode, String text)
    {
        deviceName_TV.setText(text);
    }

    @Override
    public void onQuestionYes(int requestCode, String tag)
    {
        switch (requestCode)
        {
            case REQUEST_RESTORE:
                callbacks.onGetFileContents(new BackupFileHandler(getActivity()), tag);
                break;

            case REQUEST_DELETE:
                callbacks.onDeleteFile(tag);
                break;
        }
    }

    @Override
    public void onQuestionNo(int requestCode, String tag)
    {

    }

    private void updateViews()
    {
        if (isDriveWorking)
        {
            // Fetching files from Google Drive
            driveProgress_PB.setVisibility(View.VISIBLE);
            list_V.setVisibility(View.GONE);
            notEnabledContainer_V.setVisibility(View.GONE);
            if (AppUser.getDefault(getActivity()).isDriveBackupEnabled())
            {
                buttonBar_V.setVisibility(View.VISIBLE);
                separator_V.setVisibility(View.VISIBLE);
            }
            else
            {
                buttonBar_V.setVisibility(View.GONE);
                separator_V.setVisibility(View.GONE);
            }
        }
        else
        {
            // Not fetching files
            driveProgress_PB.setVisibility(View.GONE);

            if (AppUser.getDefault(getActivity()).isDriveBackupEnabled())
            {
                // Google Drive backup is enabled
                buttonBar_V.setVisibility(View.VISIBLE);
                separator_V.setVisibility(View.VISIBLE);
                list_V.setVisibility(View.VISIBLE);
                notEnabledContainer_V.setVisibility(View.GONE);
            }
            else
            {
                // User has not enabled Google Drive backup. Ask user to enable Google Drive
                buttonBar_V.setVisibility(View.GONE);
                separator_V.setVisibility(View.GONE);
                notEnabledContainer_V.setVisibility(View.VISIBLE);
                list_V.setVisibility(View.GONE);
            }
        }
    }

    private GoogleUserFragment.GoogleRequest getWorkingRequest()
    {
        if (callbacks != null)
            return callbacks.onGetWorkingGoogleRequest();
        return null;
    }

    public static interface Callbacks
    {
        public GoogleUserFragment.GoogleRequest onGetWorkingGoogleRequest();

        public boolean onRequestFiles();

        public boolean onStoreFile(DriveFragment.FileGenerator fileGenerator, String fileToOverwriteId);

        public boolean onGetFileContents(DriveFragment.FileHandler fileHandler, String fileId);

        public boolean onDeleteFile(String fileId);
    }

    private static class BackupFileGenerator implements DriveFragment.FileGenerator
    {
        private final Context context;
        private final String deviceName;

        private BackupFileGenerator(Context context, String deviceName)
        {
            this.context = context.getApplicationContext();
            this.deviceName = deviceName;
        }

        @Override
        public java.io.File generateFile() throws Exception
        {
            return BackupUtils.generateBackupFile(context);
        }

        @Override
        public String getTitle()
        {
            return BackupUtils.BACKUP_FILE_PREFIX + System.currentTimeMillis() + BackupUtils.BACKUP_FILE_SUFFIX;
        }

        @Override
        public String getDescription()
        {
            return deviceName;
        }

        @Override
        public String getMimeType()
        {
            return BackupUtils.BACKUP_MIME_TYPE;
        }
    }

    private static class BackupFileHandler implements DriveFragment.FileHandler
    {
        private final Context context;

        private BackupFileHandler(Context context)
        {
            this.context = context.getApplicationContext();
        }

        @Override
        public void handleFileContents(InputStream is) throws Exception
        {
            new BackupParser().parseAndStore(context, new JSONObject(readInputStream(is)));
        }

        private String readInputStream(InputStream is) throws IOException
        {
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String s;

            while ((s = r.readLine()) != null)
                sb.append(s);

            return sb.toString();
        }
    }
}