package com.code44.finance.ui.tags.edit;

import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.activities.ModelEditActivity;

class TagEditData extends ModelEditActivity.ModelEditData<Tag> {
    public static final Parcelable.Creator<TagEditData> CREATOR = new Parcelable.Creator<TagEditData>() {
        public TagEditData createFromParcel(Parcel in) {
            return new TagEditData(in);
        }

        public TagEditData[] newArray(int size) {
            return new TagEditData[size];
        }
    };

    private String title;

    public TagEditData() {
        super();
    }

    public TagEditData(Parcel in) {
        super(in);
        title = in.readString();
    }

    @Override public Tag createModel() {
        final Tag tag = new Tag();
        tag.setId(getId());
        tag.setTitle(getTitle());
        return tag;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }

    public String getTitle() {
        if (title != null) {
            return title;
        }

        if (getStoredModel() != null) {
            return getStoredModel().getTitle();
        }

        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
