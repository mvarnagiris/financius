package com.code44.finance.db.model;

public class BaseModel {
    private Long _id;
    private String serverId;
    private ItemState itemState;
    private SyncState syncState;

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
