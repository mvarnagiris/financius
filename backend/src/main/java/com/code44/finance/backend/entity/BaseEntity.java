package com.code44.finance.backend.entity;

import com.code44.finance.common.model.ModelState;
import com.code44.finance.common.utils.Strings;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.UUID;

public class BaseEntity {
    @Id
    @ApiResourceProperty(name = "id")
    private String id;

    @ApiResourceProperty(name = "model_state")
    private ModelState modelState;

    @ApiResourceProperty(name = "create_ts")
    private long createTimestamp;

    @Index
    @ApiResourceProperty(name = "edit_ts")
    private long editTimestamp;

    protected BaseEntity() {
        setModelState(ModelState.Normal);
    }

    public void onCreate() {
        if (Strings.isEmpty(getId())) {
            setId(UUID.randomUUID().toString());
        }
        final long timestamp = System.currentTimeMillis();
        setCreateTimestamp(timestamp);
        setEditTimestamp(timestamp);
    }

    public void onUpdate() {
        final long timestamp = System.currentTimeMillis();
        setEditTimestamp(timestamp);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ModelState getModelState() {
        return modelState;
    }

    public void setModelState(ModelState modelState) {
        this.modelState = modelState;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public long getEditTimestamp() {
        return editTimestamp;
    }

    public void setEditTimestamp(long editTimestamp) {
        this.editTimestamp = editTimestamp;
    }
}
