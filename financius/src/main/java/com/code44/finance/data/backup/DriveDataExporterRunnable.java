package com.code44.finance.data.backup;

import android.content.Context;

import com.code44.finance.ui.settings.data.ExportActivity;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.errors.ExportError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

public class DriveDataExporterRunnable implements Runnable {
    private final GoogleApiClient googleApiClient;
    private final DriveId driveId;
    private final ExportActivity.ExportType exportType;
    private final ExportActivity.Destination destination;
    private final Context context;
    private final EventBus eventBus;
    private final String fileTitle;

    public DriveDataExporterRunnable(GoogleApiClient googleApiClient, DriveId driveId, ExportActivity.ExportType exportType, ExportActivity.Destination destination, Context context, EventBus eventBus, String fileTitle) {
        this.googleApiClient = googleApiClient;
        this.driveId = driveId;
        this.exportType = exportType;
        this.destination = destination;
        this.context = context;
        this.eventBus = eventBus;
        this.fileTitle = fileTitle;
    }

    @Override public void run() {
        final DriveFolder driveFolder = Drive.DriveApi.getFolder(googleApiClient, driveId);
        final DriveApi.DriveContentsResult result = Drive.DriveApi.newDriveContents(googleApiClient).await();

        if (!result.getStatus().isSuccess()) {
            throw new ExportError("Data export has failed.");
        }

        final DriveContents contents = result.getDriveContents();
        final DataExporterRunnable dataExporterRunnable = new DataExporterRunnable(eventBus, exportType.getDataExporter(contents.getOutputStream(), context));
        dataExporterRunnable.run();

        final MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(fileTitle).setMimeType(exportType.getMimeType(destination)).build();
        driveFolder.createFile(googleApiClient, changeSet, contents).await();
    }
}
