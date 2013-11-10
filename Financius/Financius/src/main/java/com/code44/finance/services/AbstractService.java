package com.code44.finance.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import com.code44.finance.BuildConfig;
import com.code44.finance.R;
import com.code44.finance.utils.PrefsHelper;
import de.greenrobot.event.EventBus;

public abstract class AbstractService extends IntentService
{
    public static final String EXTRA_REQUEST_TYPE = AbstractService.class.getName() + ".EXTRA_REQUEST_TYPE";
    public static final String EXTRA_FORCE = AbstractService.class.getName() + ".EXTRA_FORCE";
    public static final int RT_DEFAULT = -1;
    protected final String TAG;

    public AbstractService()
    {
        super("QuipperService");
        TAG = getClass().getSimpleName();
    }

    /**
     * Do all service work here. It's called from {@link AbstractService#onHandleIntent(Intent)}.
     *
     * @param intent      Intent passed to service.
     * @param requestType Request type from intent. For convenience.
     * @param startTime   Time when service started handling this intent.
     * @throws Exception
     */
    protected abstract void handleRequest(Intent intent, int requestType, long startTime, long lastSuccessfulWorkTime) throws Exception;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Get request type and force
        final int requestType = intent.getIntExtra(EXTRA_REQUEST_TYPE, RT_DEFAULT);
        final boolean force = intent.getBooleanExtra(EXTRA_FORCE, false);

        // Log
        if (BuildConfig.DEBUG)
        {
            final String rtTitle = getTitleForRT(intent, requestType);
            Log.i(TAG, getClass().getSimpleName() + " (RT: " + requestType + ") - Pending" + (!TextUtils.isEmpty(rtTitle) ? ". (" + rtTitle + ")" : ""));
        }

        // Send "pending" event
        final ServiceEvent workEvent = getServiceEvent(intent, requestType, force);
        if (workEvent != null)
        {
            final ServiceEvent currentEvent = (ServiceEvent) EventBus.getDefault().getStickyEvent(workEvent.getClass());
            if (currentEvent == null || currentEvent.isFinished())
            {
                workEvent.requestType = requestType;
                workEvent.state = ServiceEvent.State.PENDING;
                onBeforePostEvent(intent, requestType, workEvent.state, workEvent);
                EventBus.getDefault().postSticky(workEvent);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        // Init and get extras
        final long startTime = System.currentTimeMillis();
        final int requestType = intent.getIntExtra(EXTRA_REQUEST_TYPE, RT_DEFAULT);
        final boolean force = intent.getBooleanExtra(EXTRA_FORCE, false);
        final SharedPreferences prefs = PrefsHelper.getPrefs(getApplicationContext());
        final String prefsKey = PrefsHelper.getLastSuccessfulServiceWorkTimePrefName(getClass().getName(), requestType, getPrefsSuffix(intent, requestType));
        final long lastSuccessfulWorkTime = prefs.getLong(prefsKey, 0);
        final String rtTitle = getTitleForRT(intent, requestType);
        final String className = getClass().getSimpleName();
        ServiceEvent workEvent;

        try
        {
            // Check if need execute
            needExecute(intent, requestType, startTime, lastSuccessfulWorkTime, force);
        }
        catch (NeedExecuteException e)
        {
            // No need to execute this service

            // Log
            if (BuildConfig.DEBUG)
                Log.i(TAG, className + " (RT: " + requestType + ") - Not executed" + (!TextUtils.isEmpty(rtTitle) ? ". (" + rtTitle + ")" : "") + ". Reason: "
                        + e.getMessage());

            // Send "not executed" event
            workEvent = getServiceEvent(intent, requestType, force);
            if (workEvent != null)
            {
                workEvent.requestType = requestType;
                workEvent.state = ServiceEvent.State.NOT_EXECUTED;
                onBeforePostEvent(intent, requestType, workEvent.state, workEvent);
                EventBus.getDefault().removeStickyEvent(workEvent.getClass());
                EventBus.getDefault().post(workEvent);
            }

            return;
        }

        if (BuildConfig.DEBUG)
            Log.i(TAG, className + " (RT: " + requestType + ") - Started" + (!TextUtils.isEmpty(rtTitle) ? ". (" + rtTitle + ")" : ""));

        // Send "started" event
        workEvent = getServiceEvent(intent, requestType, force);
        if (workEvent != null)
        {
            final ServiceEvent currentEvent = (ServiceEvent) EventBus.getDefault().getStickyEvent(workEvent.getClass());
            if (currentEvent == null || currentEvent.getState() != ServiceEvent.State.STARTED)
            {
                workEvent.requestType = requestType;
                workEvent.state = ServiceEvent.State.STARTED;
                onBeforePostEvent(intent, requestType, workEvent.state, workEvent);
                EventBus.getDefault().postSticky(workEvent);
            }
        }

        try
        {
            // Check for internet connection
            if (checkForNetwork(intent, requestType) && !hasNetworkConnection(this))
                throw new NoNetworkException(this);

            // Do work
            handleRequest(intent, requestType, startTime, lastSuccessfulWorkTime);

            // Store last refresh time
            PrefsHelper.getPrefs(getApplicationContext()).edit().putLong(prefsKey, startTime).commit();

            if (BuildConfig.DEBUG)
                Log.i(TAG, className + " (RT: " + requestType + ") - Succeeded" + (!TextUtils.isEmpty(rtTitle) ? ". (" + rtTitle + ")" : ""));

            // Send "succeeded" event
            workEvent = getServiceEvent(intent, requestType, force);
            if (workEvent != null)
            {
                workEvent.requestType = requestType;
                workEvent.state = ServiceEvent.State.SUCCEEDED;
                onBeforePostEvent(intent, requestType, workEvent.state, workEvent);
                EventBus.getDefault().removeStickyEvent(workEvent.getClass());
                EventBus.getDefault().post(workEvent);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, className + " (RT: " + requestType + ") - Failed" + (!TextUtils.isEmpty(rtTitle) ? ". (" + rtTitle + ")" : ""), e);

            // Send "failed" event
            workEvent = getServiceEvent(intent, requestType, force);
            if (workEvent != null)
            {
                workEvent.requestType = requestType;
                workEvent.state = ServiceEvent.State.FAILED;
                workEvent.error = e;
                onBeforePostEvent(intent, requestType, workEvent.state, workEvent);
                EventBus.getDefault().removeStickyEvent(workEvent.getClass());
                EventBus.getDefault().post(workEvent);
            }
        }
    }

    /**
     * Create work event for current service.
     *
     * @param intent      Intent set to service.
     * @param requestType Request type.
     * @param force       Force value from intent.
     * @return Work event with proper request type set
     */
    protected ServiceEvent getServiceEvent(Intent intent, int requestType, boolean force)
    {
        return null;
    }

    /**
     * Determine if work needs to be done or not.
     *
     * @param intent      Intent passed to service.
     * @param requestType Request type from intent. For convenience.
     * @param startTime   Time when service started handling this intent.
     * @param force       Force value from intent. For convenience.
     * @throws NeedExecuteException When work does not need to be done.
     */
    protected void needExecute(Intent intent, int requestType, long startTime, long lastSuccessfulWokTime, boolean force) throws NeedExecuteException
    {
    }

    /**
     * Called when building key to get/store last successful work time.
     * <p>
     * Note: You don't need to include request type, because it's already included in the name.
     * </p>
     *
     * @param intent      Intent.
     * @param requestType Request type.
     * @return Suffix for preferences key. Can be {@code null}.
     */
    protected String getPrefsSuffix(Intent intent, int requestType)
    {
        return "";
    }

    /**
     * Override this method and return title for request type for better logging.
     *
     * @param intent      Intent passed to service.
     * @param requestType Request type.
     * @return Title for request type, or {@code null}.
     */
    protected String getTitleForRT(Intent intent, int requestType)
    {
        return null;
    }

    /**
     * Override this if you don't want to check for internet for specific request types.
     *
     * @param intent      Intent passed to service.
     * @param requestType Request type.
     * @return {@code true} if need to check for network connection; {@code false} otherwise.
     */
    protected boolean checkForNetwork(Intent intent, int requestType)
    {
        return true;
    }

    /**
     * Checks if device has any network connectivity at the moment.
     * <p>
     * Requires {@link android.Manifest.permission#ACCESS_NETWORK_STATE}.
     * </p>
     *
     * @param context Context
     * @return {@code true} if device has network connection; {@code false} otherwise.
     */
    protected boolean hasNetworkConnection(Context context)
    {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    protected void onBeforePostEvent(Intent intent, int requestTime, ServiceEvent.State state, ServiceEvent outEvent)
    {
    }

    public static class ServiceEvent
    {
        private final boolean force;
        private int requestType;
        private State state;
        private Throwable error = null;

        public ServiceEvent(int requestType, boolean force)
        {
            this.requestType = requestType;
            this.force = force;
        }

        public int getRequestType()
        {
            return requestType;
        }

        public boolean isForce()
        {
            return force;
        }

        public State getState()
        {
            return state;
        }

        public Throwable getError()
        {
            return error;
        }

        public boolean isWorking(boolean isWorkingWhenPending)
        {
            switch (state)
            {
                case PENDING:
                    return isWorkingWhenPending;
                case STARTED:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isFinished()
        {
            switch (state)
            {
                case SUCCEEDED:
                case FAILED:
                case NOT_EXECUTED:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isSuccessful()
        {
            switch (state)
            {
                case SUCCEEDED:
                    return true;
                default:
                    return false;
            }
        }

        public enum State
        {
            PENDING, STARTED, SUCCEEDED, FAILED, NOT_EXECUTED
        }
    }

    /**
     * Thrown when work does not need to be executed.
     *
     * @author Mantas Varnagiris
     */
    protected static class NeedExecuteException extends Exception
    {
        private static final long serialVersionUID = 1680639942049548461L;

        public NeedExecuteException(final String reason)
        {
            super(reason);
        }
    }

    /**
     * Thrown when there is no internet connection.
     */
    public static class NoNetworkException extends Exception
    {
        public NoNetworkException(Context context)
        {
            super(context.getString(R.string.l_no_internet_error));
        }
    }

}