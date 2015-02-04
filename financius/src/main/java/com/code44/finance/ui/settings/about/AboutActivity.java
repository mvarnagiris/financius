package com.code44.finance.ui.settings.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.BuildConfig;
import com.code44.finance.R;
import com.code44.finance.ui.common.activities.BaseActivity;
import com.code44.finance.utils.analytics.Analytics;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

public class AboutActivity extends BaseActivity implements View.OnClickListener {
    public static void start(Context context) {
        startActivity(context, makeIntentForActivity(context, AboutActivity.class));
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Get views
        final TextView version_TV = (TextView) findViewById(R.id.version_TV);
        final TextView buildTime_TV = (TextView) findViewById(R.id.buildTime_TV);
        final Button changeLog_B = (Button) findViewById(R.id.changeLog_B);
        final Button issues_B = (Button) findViewById(R.id.issues_B);
        final Button rate_B = (Button) findViewById(R.id.rate_B);
        final Button community_B = (Button) findViewById(R.id.community_B);
        final Button translate_B = (Button) findViewById(R.id.translate_B);

        // Setup
        version_TV.setText(BuildConfig.VERSION_NAME);
        buildTime_TV.setText(DateUtils.formatDateTime(this, new DateTime(BuildConfig.BUILD_TIME), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
        changeLog_B.setOnClickListener(this);
        issues_B.setOnClickListener(this);
        rate_B.setOnClickListener(this);
        community_B.setOnClickListener(this);
        translate_B.setOnClickListener(this);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        return true;
    }

    @Override protected Analytics.Screen getScreen() {
        return Analytics.Screen.About;
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changeLog_B:
                showChangeLog();
                break;
            case R.id.issues_B:
                showIssues();
                break;
            case R.id.rate_B:
                showRate();
                break;
            case R.id.community_B:
                showCommunity();
                break;
            case R.id.translate_B:
                showTranslate();
                break;
        }
    }

    private void showChangeLog() {
        openUrl("https://github.com/mvarnagiris/Financius-public/blob/dev/CHANGELOG.md");
    }

    private void showIssues() {
        openUrl("https://github.com/mvarnagiris/Financius-public/blob/dev/README.md");
    }

    private void showRate() {
        openUrl("https://play.google.com/store/apps/details?id=com.code44.finance");
    }

    private void showCommunity() {
        openUrl("https://plus.google.com/communities/105052097023793642366");
    }

    private void showTranslate() {
        openUrl("https://crowdin.com/project/financius");
    }

    private void openUrl(String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}