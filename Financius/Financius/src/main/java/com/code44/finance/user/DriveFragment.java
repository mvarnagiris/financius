package com.code44.finance.user;

public class DriveFragment extends GoogleUserFragment
{
    public static DriveFragment newInstance()
    {
        DriveFragment f = new DriveFragment();
        return f;
    }

//    private static Drive getDrive(GoogleAccountCredential credential)
//    {
//        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
//    }

//    public void getFiles()
//    {
//        addNewRequest(new GetFilesRequest());
//    }
//
//    public void storeFile(FileGenerator fileGenerator, String fileToOverwriteId)
//    {
//        addNewRequest(new StoreFileRequest(fileGenerator, fileToOverwriteId));
//    }
//
//    public void getFileContents(FileHandler fileHandler, String fileId)
//    {
//        addNewRequest(new GetFileContentsRequest(fileHandler, fileId));
//    }
//
//    public void deleteFile(String fileId)
//    {
//        addNewRequest(new DeleteFileRequest(fileId));
//    }
//
//    public static interface FileGenerator
//    {
//        public java.io.File generateFile() throws Exception;
//
//        public String getTitle();
//
//        public String getDescription();
//
//        public String getMimeType();
//    }
//
//    public static interface FileHandler
//    {
//        public void handleFileContents(InputStream is) throws Exception;
//    }
//
//    private static abstract class DriveRequest implements GoogleRequest
//    {
//        @Override
//        public List<String> getScopes()
//        {
//            final List<String> outScopes = new ArrayList<String>();
//            outScopes.add("https://www.googleapis.com/auth/drive.appdata");
//            return outScopes;
//        }
//    }
//
//    public static class GetFilesRequest extends DriveRequest
//    {
//        @Override
//        public GoogleUserFragment.GoogleResult execute(GoogleAccountCredential credential) throws Exception
//        {
//            // Get Drive service
//            final Drive drive = getDrive(credential);
//
//            // Prepare result list
//            final List<File> result = new ArrayList<File>();
//            final Drive.Files.List request = drive.files().list();
//            request.setQ("'appdata' in parents");
//
//            do
//            {
//                try
//                {
//                    final FileList files = request.execute();
//
//                    result.addAll(files.getItems());
//                    request.setPageToken(files.getNextPageToken());
//                }
//                catch (IOException e)
//                {
//                    request.setPageToken(null);
//                    throw e;
//                }
//            }
//            while (!TextUtils.isEmpty(request.getPageToken()));
//
//            return new GetFilesResult(this, result);
//        }
//    }
//
//    public static class StoreFileRequest extends DriveRequest
//    {
//        private FileGenerator fileGenerator;
//        private String fileToOverwriteId;
//
//        private StoreFileRequest(FileGenerator fileGenerator, String fileToOverwriteId)
//        {
//            this.fileGenerator = fileGenerator;
//            this.fileToOverwriteId = fileToOverwriteId;
//        }
//
//        @Override
//        public GoogleResult execute(GoogleAccountCredential credential) throws Exception
//        {
//            // Get Drive service
//            final Drive drive = getDrive(credential);
//
//            // File's metadata.
//            File body = new File();
//            body.setTitle(fileGenerator.getTitle());
//            body.setDescription(fileGenerator.getDescription());
//            body.setMimeType(fileGenerator.getMimeType());
//            body.setParents(Arrays.asList(new ParentReference().setId("appdata")));
//
//            // File's content.
//            java.io.File fileContent = fileGenerator.generateFile();
//            FileContent mediaContent = new FileContent(fileGenerator.getMimeType(), fileContent);
//            File file;
//            if (TextUtils.isEmpty(fileToOverwriteId))
//                file = drive.files().insert(body, mediaContent).execute();
//            else
//                file = drive.files().update(fileToOverwriteId, body, mediaContent).execute();
//
//            return new StoreFileResult(this, file);
//        }
//    }
//
//    public static class GetFileContentsRequest extends DriveRequest
//    {
//        private FileHandler fileHandler;
//        private String fileId;
//
//        private GetFileContentsRequest(FileHandler fileHandler, String fileId)
//        {
//            this.fileHandler = fileHandler;
//            this.fileId = fileId;
//        }
//
//        @Override
//        public GoogleResult execute(GoogleAccountCredential credential) throws Exception
//        {
//            // Get Drive service
//            final Drive drive = getDrive(credential);
//
//            // Get file's metadata.
//            File file = drive.files().get(fileId).execute();
//
//            if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0)
//            {
//                HttpResponse resp = drive.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
//                fileHandler.handleFileContents(resp.getContent());
//            }
//            else
//                throw new IOException("Could not download file contents");
//
//            return new GoogleResult(this);
//        }
//    }
//
//    public static class DeleteFileRequest extends DriveRequest
//    {
//        private final String fileId;
//
//        private DeleteFileRequest(String fileId)
//        {
//            this.fileId = fileId;
//        }
//
//        @Override
//        public GoogleResult execute(GoogleAccountCredential credential) throws Exception
//        {
//            // Get Drive service
//            final Drive drive = getDrive(credential);
//
//            // Delete file
//            drive.files().delete(fileId).execute();
//
//
//            return new GoogleResult(this);
//        }
//    }
//
//    public static class GetFilesResult extends GoogleResult
//    {
//        List<File> files;
//
//        public GetFilesResult(GoogleRequest request, List<File> files)
//        {
//            super(request);
//            this.files = files;
//        }
//
//        public List<File> getFiles()
//        {
//            return files;
//        }
//    }
//
//    public static class StoreFileResult extends GoogleResult
//    {
//        private final File file;
//
//        public StoreFileResult(GoogleRequest request, File file)
//        {
//            super(request);
//            this.file = file;
//        }
//
//        public File getFile()
//        {
//            return file;
//        }
//    }
}