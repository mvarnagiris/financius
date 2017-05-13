package com.code44.finance.ui.common.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.DataStore;
import com.code44.finance.data.model.Model;

public abstract class ModelEditActivity<M extends Model, MED extends ModelEditActivity.ModelEditData<M>> extends ModelActivity<M> {
    private static final String STATE_MODEL_EDIT_DATA = "STATE_MODEL_EDIT_DATA";

    private MED modelEditData;
    private ModelEditValidator<MED> modelEditValidator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            modelEditData = savedInstanceState.getParcelable(STATE_MODEL_EDIT_DATA);
        } else {
            modelEditData = createModelEditData();
        }
    }

    @Override protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        modelEditValidator = createModelEditValidator();
    }

    @Override protected void onResume() {
        super.onResume();
        if (isNewModel()) {
            onDataChanged(modelEditData);
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MODEL_EDIT_DATA, modelEditData);
    }

    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // Get views
        final Button cancelView = (Button) findViewById(R.id.cancelButton);
        final Button saveView = (Button) findViewById(R.id.saveButton);

        // Setup
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                cancel();
            }
        });
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                save();
            }
        });
    }

    @Override protected void onModelLoaded(@NonNull M model) {
        modelEditData.storedModel = model;
        onDataChanged(modelEditData);
    }

    @Override protected void startModelEdit(@NonNull String modelId) {
    }

    @Nullable @Override protected Uri getDeleteUri() {
        return null;
    }

    @Nullable @Override protected Pair<String, String[]> getDeleteSelection(@NonNull String modelId) {
        return null;
    }

    @NonNull protected abstract MED createModelEditData();

    @NonNull protected abstract ModelEditValidator<MED> createModelEditValidator();

    protected abstract void onDataChanged(@NonNull MED modelEditData);

    @NonNull protected abstract Uri getSaveUri();

    protected boolean isNewModel() {
        return getModelId().equals("0");
    }

    @NonNull protected MED getModelEditData() {
        return modelEditData;
    }

    @NonNull protected ModelEditValidator<MED> getModelEditValidator() {
        return modelEditValidator;
    }

    protected void onAfterSave(@NonNull M model) {
    }

    private void save() {
        if (modelEditValidator.validate(modelEditData, true)) {
            final M model = modelEditData.createModel();
            DataStore.insert().model(model).into(this, getSaveUri());
            onAfterSave(model);
            finish();
        }
    }

    private void cancel() {
        finish();
    }

    public interface ModelEditValidator<MED extends ModelEditActivity.ModelEditData<?>> {
        boolean validate(@NonNull MED modelEditData, boolean showError);
    }

    public static abstract class ModelEditData<M extends Model> implements Parcelable {
        M storedModel;

        public ModelEditData() {
        }

        protected ModelEditData(Parcel in) {
        }

        @Override public int describeContents() {
            return 0;
        }

        public abstract M createModel();

        public String getId() {
            return getStoredModel() != null ? getStoredModel().getId() : null;
        }

        public M getStoredModel() {
            return storedModel;
        }
    }
}
