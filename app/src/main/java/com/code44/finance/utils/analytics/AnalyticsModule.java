package com.code44.finance.utils.analytics;

import android.content.Context;

import com.code44.finance.ApplicationContext;
import com.code44.finance.BuildConfig;
import com.code44.finance.utils.preferences.GeneralPrefs;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false)
public class AnalyticsModule {
    private static final String APP_TRACKER_ID = "UA-38249360-1";
    private static final long SESSION_TIMEOUT = 3 * 60; // 3 minutes

    @Provides public GoogleAnalytics provideGoogleAnalytics(@ApplicationContext Context context, GeneralPrefs generalPrefs) {
        final GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        googleAnalytics.setDryRun(BuildConfig.DEBUG);
        googleAnalytics.setAppOptOut(generalPrefs.isAnalyticsOptOut());
        return googleAnalytics;
    }

    @Provides @Singleton @AppTracker public Tracker provideAppTracker(GoogleAnalytics googleAnalytics) {
        final Tracker tracker = googleAnalytics.newTracker(APP_TRACKER_ID);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableExceptionReporting(true);
        tracker.enableAutoActivityTracking(false);
        tracker.setSessionTimeout(SESSION_TIMEOUT);
        return tracker;
    }
}
