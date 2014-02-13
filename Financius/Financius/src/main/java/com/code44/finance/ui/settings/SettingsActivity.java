package com.code44.finance.ui.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.view.MenuItem;
import android.widget.Toast;

import com.code44.finance.R;
import com.code44.finance.ui.AboutActivity;
import com.code44.finance.ui.backup.YourDataActivity;
import com.code44.finance.ui.categories.CategoryListActivity;
import com.code44.finance.ui.currencies.CurrencyListActivity;
import com.code44.finance.ui.settings.donate.DonateActivity;
import com.code44.finance.ui.settings.lock.LockActivity;
import com.code44.finance.utils.ExchangeRatesHelper;
import com.code44.finance.utils.PeriodHelper;
import com.code44.finance.utils.PrefsHelper;
import com.code44.finance.utils.SecurityHelper;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, OnPreferenceClickListener, Preference.OnPreferenceChangeListener
{
    public static final String PREF_PERIOD = "period";
    public static final String PREF_CURRENCIES = "currencies";
    public static final String PREF_UPDATE_EXCHANGE_RATES = "update_exchange_rates";
    public static final String PREF_CATEGORIES = "categories";
    public static final String PREF_FOCUS_CATEGORIES_SEARCH = "focus_categories_search";
    public static final String PREF_APP_LOCK = "app_lock";
    public static final String PREF_APP_LOCK_PATTERN = "app_lock_pattern";
    public static final String PREF_APP_LOCK_NONE = "app_lock_none";
    public static final String PREF_DONATE = "donate";
    public static final String PREF_RATE_APP = "rate_app";
    public static final String PREF_CHANGE_LOG = "change_log";
    public static final String PREF_YOUR_DATA = "your_data";
    // -----------------------------------------------------------------------------------------------------------------
    private ListPreference period_P;
    private Preference appLock_P;
    private Preference changeLog_P;
    private ListPreference updateExchangeRates_P;

    public static void startSettings(Context context)
    {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // Setup ActionBar
        final String title = getString(R.string.settings);
        SpannableStringBuilder ssb = new SpannableStringBuilder(title);
        ssb.setSpan(new TypefaceSpan("sans-serif-light"), 0, title.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        getActionBar().setTitle(ssb);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Get preferences
        period_P = (ListPreference) findPreference(PREF_PERIOD);
        updateExchangeRates_P = (ListPreference) findPreference(PREF_UPDATE_EXCHANGE_RATES);
        appLock_P = findPreference(PREF_APP_LOCK);
        changeLog_P = findPreference(PREF_CHANGE_LOG);

        // Set OnPreferenceClickListener
        findPreference(PREF_CURRENCIES).setOnPreferenceClickListener(this);
        findPreference(PREF_CATEGORIES).setOnPreferenceClickListener(this);
        findPreference(PREF_APP_LOCK_PATTERN).setOnPreferenceClickListener(this);
        findPreference(PREF_APP_LOCK_NONE).setOnPreferenceClickListener(this);
        findPreference(PREF_DONATE).setOnPreferenceClickListener(this);
        findPreference(PREF_RATE_APP).setOnPreferenceClickListener(this);
        findPreference(PREF_YOUR_DATA).setOnPreferenceClickListener(this);
        changeLog_P.setOnPreferenceClickListener(this);

        // Set OnPreferenceChangeListener
        period_P.setOnPreferenceChangeListener(this);
        updateExchangeRates_P.setOnPreferenceChangeListener(this);
        findPreference(PREF_FOCUS_CATEGORIES_SEARCH).setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updatePreferencesWithContentChanged();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause()
    {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        updatePreferences();
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        if (preference.getKey().equals(PREF_CURRENCIES))
        {
            CurrencyListActivity.startList(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_CATEGORIES))
        {
            CategoryListActivity.startList(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_APP_LOCK_PATTERN))
        {
            LockActivity.startLockNewPattern(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_APP_LOCK_NONE))
        {
            LockActivity.startLockClear(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_DONATE))
        {
            DonateActivity.startDonate(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_RATE_APP))
        {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try
            {
                startActivity(goToMarket);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(SettingsActivity.this, "Couldn't launch the market", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        else if (preference.getKey().equals(PREF_CHANGE_LOG))
        {
            AboutActivity.startAbout(SettingsActivity.this);
            return true;
        }
        else if (preference.getKey().equals(PREF_YOUR_DATA))
        {
            YourDataActivity.start(SettingsActivity.this);
            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue)
    {
        if (preference.getKey().equals(PREF_PERIOD))
        {
            PeriodHelper.getDefault(SettingsActivity.this).setType(Integer.parseInt((String) newValue));
            return true;
        }
        else if (preference.getKey().equals(PREF_UPDATE_EXCHANGE_RATES))
        {
            ExchangeRatesHelper.getDefault(SettingsActivity.this).setExchangeRatesValue((String) newValue);
            return true;
        }
        else if (preference.getKey().equals(PREF_FOCUS_CATEGORIES_SEARCH))
        {
            PrefsHelper.getDefault(this).setFocusCategoriesSearch((Boolean) newValue);
            return true;
        }
        return false;
    }

    private void updatePreferencesWithContentChanged()
    {
        // Period
        switch (PeriodHelper.getDefault(this).getType())
        {
            case PeriodHelper.TYPE_YEAR:
                period_P.setSummary(R.string.year);
                break;

            case PeriodHelper.TYPE_MONTH:
                period_P.setSummary(R.string.month);
                break;

            case PeriodHelper.TYPE_WEEK:
                period_P.setSummary(R.string.week);
                break;

            case PeriodHelper.TYPE_DAY:
                period_P.setSummary(R.string.day);
                break;
        }

        // App lock
        final int appLock = SecurityHelper.getDefault(this).getAppLockCode();
        switch (appLock)
        {
            case SecurityHelper.APP_LOCK_PATTERN:
                appLock_P.setSummary(R.string.pattern);
                break;

            default:
                appLock_P.setSummary(R.string.none);
                break;
        }

        // Change log
        try
        {
            changeLog_P.setSummary("v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (NameNotFoundException e)
        {
            // Ignore
        }

        updatePreferences();
        onContentChanged();
    }

    private void updatePreferences()
    {
        // Update exchange rates
        updateExchangeRates_P.setSummary(updateExchangeRates_P.getEntry());
        period_P.setSummary(period_P.getEntry());
    }
}