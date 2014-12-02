package com.code44.finance.ui.transactions;

import android.content.Intent;
import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.ModelListActivity;
import com.code44.finance.utils.TextBackgroundSpan;

import java.util.ArrayList;
import java.util.List;

public class TagsController implements View.OnClickListener, View.OnLongClickListener {
    private final Button tagsButton;
    private final Callbacks callbacks;
    private final List<Tag> tags;
    private final int tagBackgroundColor;
    private final float tagBackgroundRadius;

    public TagsController(Button tagsButton, Callbacks callbacks) {
        this.tagsButton = tagsButton;
        this.tagsButton.setOnClickListener(this);
        this.tagsButton.setOnLongClickListener(this);
        this.callbacks = callbacks;
        this.tags = new ArrayList<>();

        final Resources res = tagsButton.getResources();
        tagBackgroundColor = res.getColor(R.color.bg_secondary);
        tagBackgroundRadius = res.getDimension(R.dimen.tag_radius);
    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tagsButton:
                callbacks.onRequestTags(tags);
                break;
        }
    }

    @Override public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.tagsButton:
                tags.clear();
                updateViews();
                callbacks.onTagsUpdated(tags);
                return true;
        }
        return false;
    }

    public void handleActivityResult(Intent data) {
        setTags(ModelListActivity.<Tag>getModelsExtra(data));
        callbacks.onTagsUpdated(tags);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
        updateViews();
    }

    private void updateViews() {
        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Tag tag : this.tags) {
            ssb.append(tag.getTitle());
            ssb.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), ssb.length() - tag.getTitle().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        tagsButton.setText(ssb);
    }

    public static interface Callbacks {
        public void onRequestTags(List<Tag> tags);

        public void onTagsUpdated(List<Tag> tags);
    }
}
