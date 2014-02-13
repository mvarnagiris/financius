package com.code44.finance.user;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GoogleUserFragment extends Fragment
{
    private static final int REQUEST_ACCOUNT = 16847;
    private static final int REQUEST_AUTHORIZATION = 26843;
    private Set<GoogleRequest> pendingRequests = new HashSet<GoogleRequest>();
    private Callbacks callbacks;
    private GoogleUser googleUser;
    private ExecutorAsyncTask executorAsyncTask = null;

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

        // Tell the framework to try to keep this fragment around during a configuration change
        setRetainInstance(true);

        // Init
        googleUser = GoogleUser.getDefault(getActivity());
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        // Unlink from activity
        callbacks = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACCOUNT:
                if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null)
                {
                    // Account was selected
                    googleUser.setAccountName(data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                    executeNextRequest();
                }
                else
                {
                    // Authorization failed.
                    onAuthorizationFailed();
                }
                break;

            case REQUEST_AUTHORIZATION:
                if (resultCode == Activity.RESULT_OK)
                {
                    // Authorization was successful
                    executeNextRequest();
                }
                else
                {
                    // Authorization failed.
                    onAuthorizationFailed();
                }
                break;
        }
    }

    public void setCallbacksListener(Callbacks callbacks)
    {
        this.callbacks = callbacks;
    }

    public GoogleRequest getWorkingRequest()
    {
        return executorAsyncTask != null && executorAsyncTask.getStatus() != AsyncTask.Status.FINISHED && !executorAsyncTask.isCancelled() ? executorAsyncTask.getRequest() : null;
    }

    protected void addNewRequest(GoogleRequest request)
    {
        if (pendingRequests.add(request) && callbacks != null)
            callbacks.onRequestPending(request);

        if (!googleUser.hasAccount())
        {
            // If we don't have account selected, ask user to select
//            startActivityForResult(getAccountChooserIntent(request.getScopes()), REQUEST_ACCOUNT);
        }
        else
            executeNextRequest();
    }

//    private GoogleAccountCredential getCredential(GoogleRequest request)
//    {
//        // Setup credential
//        final GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(), request.getScopes());
//        credential.setSelectedAccountName(googleUser.getAccountName());
//        return credential;
//    }
//
//    private Intent getAccountChooserIntent(List<String> scopes)
//    {
//        return GoogleAccountCredential.usingOAuth2(getActivity(), scopes).newChooseAccountIntent();
//    }

    private void onAuthorizationFailed()
    {
        pendingRequests.clear();
        googleUser.setAccountName(null);
        if (callbacks != null)
            callbacks.onAuthorizationFailed();
    }

    private void executeNextRequest()
    {
        if ((executorAsyncTask == null || executorAsyncTask.isCancelled() || executorAsyncTask.getStatus() == AsyncTask.Status.FINISHED) && pendingRequests.size() > 0)
        {
            final GoogleRequest request = pendingRequests.iterator().next();
            executorAsyncTask = new ExecutorAsyncTask(request);
            executorAsyncTask.execute();
        }
        else
        {
            executorAsyncTask = null;
        }
    }

    public static interface GoogleRequest
    {
        //public GoogleResult execute(GoogleAccountCredential credential) throws Exception;

        public List<String> getScopes();
    }

    public static interface Callbacks
    {
        public void onRequestPending(GoogleRequest request);

        public void onRequestStarted(GoogleRequest request);

        public void onRequestSucceeded(GoogleResult result);

        public void onRequestFailed(GoogleRequest request, Exception e);

        public void onAuthorizationFailed();
    }

    public static class GoogleResult
    {
        private final GoogleRequest request;
        private Exception exception;

        public GoogleResult(GoogleRequest request)
        {
            this.request = request;
        }

        public GoogleRequest getRequest()
        {
            return request;
        }

        public Exception getException()
        {
            return exception;
        }

        public void setException(Exception exception)
        {
            this.exception = exception;
        }

        // TODO Generate Hash and Equals
    }

    private class ExecutorAsyncTask extends AsyncTask<Void, Void, GoogleResult>
    {
        private final GoogleRequest request;

        public ExecutorAsyncTask(GoogleRequest request)
        {
            this.request = request;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (callbacks != null)
                callbacks.onRequestStarted(request);
        }

        @Override
        protected GoogleResult doInBackground(Void... params)
        {
//            GoogleResult result;
//            try
//            {
//                result = request.execute(getCredential(request));
//            }
//            catch (Exception e)
//            {
//                result = new GoogleResult(request);
//                result.setException(e);
//            }
//
//            return result;
            return null;
        }

        @Override
        protected void onPostExecute(GoogleResult googleResult)
        {
            super.onPostExecute(googleResult);

            executorAsyncTask = null;
            try
            {
                // Check if request failed and throw exception if it did.
                if (googleResult.getException() != null)
                    throw googleResult.getException();

                // Request succeeded.
                pendingRequests.remove(googleResult.getRequest());
                if (callbacks != null)
                    callbacks.onRequestSucceeded(googleResult);

                executeNextRequest();
            }
//            catch (UserRecoverableAuthIOException e)
//            {
//                // User can recover from error
//                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
//            }
            catch (Exception e)
            {
                // Request failed
                pendingRequests.remove(googleResult.getRequest());
                if (callbacks != null)
                    callbacks.onRequestFailed(googleResult.getRequest(), e);
            }
        }

        private GoogleRequest getRequest()
        {
            return request;
        }
    }
}
