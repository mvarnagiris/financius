package com.code44.finance.ui.user;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.api.endpoints.User;
import com.code44.finance.services.LogoutService;
import com.code44.finance.ui.common.activities.ActivityStarter;
import com.code44.finance.ui.common.activities.DrawerActivity;
import com.code44.finance.ui.common.navigation.NavigationScreen;
import com.code44.finance.utils.analytics.Screens;
import com.code44.finance.utils.picasso.PicassoUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ProfileActivity extends DrawerActivity {
    @Inject User user;

    private ImageView coverImageView;
    private ImageView photoImageView;
    private TextView nameTextView;

    public static void start(Context context) {
        ActivityStarter.begin(context, ProfileActivity.class).topLevel().showDrawer().showDrawerToggle().start();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get views
        final View containerView = findViewById(R.id.containerView);
        coverImageView = (ImageView) findViewById(R.id.coverImageView);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);

        // Setup
        onUserChanged(user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            photoImageView.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            });
            containerView.setOutlineProvider(new ViewOutlineProvider() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP) @Override public void getOutline(View view, Outline outline) {
                    outline.setRect(0, 0, view.getWidth(), view.getHeight());
                }
            });
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected NavigationScreen getNavigationScreen() {
        return NavigationScreen.User;
    }

    @NonNull @Override protected Screens.Screen getScreen() {
        return Screens.Screen.Profile;
    }

    @Subscribe public void onUserChanged(User user) {
        PicassoUtils.loadUserCover(coverImageView, user.getCoverUrl());
        PicassoUtils.loadUserPhoto(photoImageView, user.getPhotoUrl());
        nameTextView.setText(user.getName());
    }

    private void logout() {
        LogoutService.start(this);
    }
}
