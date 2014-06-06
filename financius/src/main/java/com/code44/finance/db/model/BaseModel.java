package com.code44.finance.db.model;

import android.text.TextUtils;

import java.util.UUID;

public abstract class BaseModel {
    private Long _id;
    private String serverId;
    private ItemState itemState;
    private SyncState syncState;

    protected BaseModel() {
        setItemState(ItemState.NORMAL);
        setSyncState(SyncState.NONE);
    }

    public void useDefaultsIfNotSet() {
        if (TextUtils.isEmpty(serverId)) {
            setServerId(UUID.randomUUID().toString());
        }

        if (itemState == null) {
            itemState = ItemState.NORMAL;
        }

        if (syncState == null) {
            syncState = SyncState.NONE;
        }
    }

    public void checkRequiredValues() throws IllegalStateException {
        if (TextUtils.isEmpty(serverId)) {
            throw new IllegalStateException("Server Id cannot be empty.");
        }

        if (itemState == null) {
            throw new IllegalStateException("ItemState cannot be null.");
        }

        if (syncState == null) {
            throw new IllegalStateException("SyncState cannot be null.");
        }
    }

    public Long getId() {
        return _id;
    }

    public void setId(Long id) {
        this._id = id;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public ItemState getItemState() {
        return itemState;
    }

    public void setItemState(ItemState itemState) {
        this.itemState = itemState;
    }

    public SyncState getSyncState() {
        return syncState;
    }

    public void setSyncState(SyncState syncState) {
        this.syncState = syncState;
    }

    public static enum ItemState {
        NORMAL, DELETED, DELETED_UNDO
    }

    public static enum SyncState {
        NONE, IN_PROGRESS, SYNCED, LOCAL_CHANGES
    }
}
