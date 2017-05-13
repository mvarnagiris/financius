package com.code44.finance.ui.common.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;

import com.code44.finance.data.model.Model;

import java.io.Serializable;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class ActivityStarter {
    private final Fragment fragment;
    private final Context context;
    private final Class activityClass;
    private final Intent intent;

    private ActivityStarter(@NonNull Context context, @NonNull Class activityClass) {
        this.fragment = null;
        this.context = checkNotNull(context, "Context cannot be null.");
        this.activityClass = checkNotNull(activityClass, "Activity class cannot be null.");
        this.intent = createIntent(context, activityClass);
    }

    private ActivityStarter(@NonNull Fragment fragment, @NonNull Class activityClass) {
        this.fragment = checkNotNull(fragment, "Fragment cannot be null.");
        this.context = checkNotNull(fragment.getActivity(), "Fragment must be attached to Activity.");
        this.activityClass = checkNotNull(activityClass, "Activity class cannot be null.");
        this.intent = createIntent(context, activityClass);
    }

    public static ActivityStarter begin(@NonNull Context context, @NonNull Class activityClass) {
        return new ActivityStarter(context, activityClass);
    }

    public static ActivityStarter begin(@NonNull Fragment fragment, @NonNull Class activityClass) {
        return new ActivityStarter(fragment, activityClass);
    }

    public ActivityStarter topLevel() {
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return this;
    }

    public ActivityStarter showDrawer() {
        validateDrawerActivity();
        intent.putExtra(DrawerActivity.EXTRA_SHOW_DRAWER, true);
        return this;
    }

    public ActivityStarter showDrawerToggle() {
        validateDrawerActivity();
        intent.putExtra(DrawerActivity.EXTRA_SHOW_DRAWER_TOGGLE, true);

        if (!intent.getBooleanExtra(DrawerActivity.EXTRA_SHOW_DRAWER, false)) {
            throw new IllegalStateException("Cannot show " + ActionBarDrawerToggle.class.getSimpleName() + " if drawer is not shown. Call setShowDrawer(true) before calling this method.");
        }

        return this;
    }

    public ActivityStarter modelsView() {
        validateModelsActivity();
        intent.putExtra(ModelsActivity.EXTRA_MODE, ModelsActivity.Mode.View);
        return this;
    }

    public ActivityStarter modelsSelect() {
        validateModelsActivity();
        intent.putExtra(ModelsActivity.EXTRA_MODE, ModelsActivity.Mode.Select);
        return this;
    }

    public ActivityStarter modelsMultiSelect(Collection<? extends Model> selectedModels) {
        validateModelsActivity();
        intent.putExtra(ModelsActivity.EXTRA_MODE, ModelsActivity.Mode.MultiSelect);
        final Parcelable[] parcelables = new Parcelable[selectedModels.size()];
        int index = 0;
        for (Model model : selectedModels) {
            parcelables[index++] = model;
        }
        intent.putExtra(ModelsActivity.EXTRA_SELECTED_MODELS, parcelables);
        return this;
    }

    public ActivityStarter extra(String name, Parcelable value) {
        intent.putExtra(name, value);
        return this;
    }

    public ActivityStarter extra(String name, Serializable value) {
        intent.putExtra(name, value);
        return this;
    }

    public ActivityStarter extra(String name, String value) {
        intent.putExtra(name, value);
        return this;
    }

    public ActivityStarter extra(String name, boolean value) {
        intent.putExtra(name, value);
        return this;
    }

    public ActivityStarter extra(String name, long value) {
        intent.putExtra(name, value);
        return this;
    }

    public ActivityStarter extra(String name, double value) {
        intent.putExtra(name, value);
        return this;
    }

    public ActivityStarter addFlags(int flags) {
        intent.addFlags(flags);
        return this;
    }

    public void start() {
        if (fragment != null) {
            fragment.startActivity(intent);
        } else {
            context.startActivity(intent);
        }
    }

    public void startForResult(int requestCode) {
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            throw new IllegalArgumentException("Context must be an Activity, when starting for result.");
        }
    }

    private Intent createIntent(Context context, Class activityClass) {
        return new Intent(context, activityClass);
    }

    private void validateDrawerActivity() {
        if (!DrawerActivity.class.isAssignableFrom(activityClass)) {
            throw new IllegalStateException(activityClass.getName() + " must extend " + DrawerActivity.class.getName());
        }
    }

    private void validateModelsActivity() {
        if (!ModelsActivity.class.isAssignableFrom(activityClass)) {
            throw new IllegalStateException(activityClass.getName() + " must extend " + ModelsActivity.class.getName());
        }

        if (intent.hasExtra(ModelsActivity.EXTRA_MODE)) {
            throw new IllegalStateException("Intent already has " + ModelsActivity.Mode.class.getName() + "=" + intent.getSerializableExtra(ModelsActivity.EXTRA_MODE));
        }
    }
}
