package com.code44.finance.backend.entity;

import com.code44.finance.common.model.ModelState;
import com.googlecode.objectify.annotation.Id;

import java.util.UUID;

public class BaseEntity {
    @Id
    private String id;
    private ModelState modelState;
    private long createTimestamp;
    private long editTimestamp;

    protected BaseEntity() {
        setModelState(ModelState.NORMAL);
    }

    public void onCreate() {
        setId(UUID.randomUUID().toString());
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
