package com.code44.finance.ui.transactions.controllers;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.ViewController;
import com.code44.finance.utils.TextBackgroundSpan;

import java.util.Collections;
import java.util.List;

public class TagsViewController extends ViewController {
    private final Button tagsButton;
    private final int tagBackgroundColor;
    private final float tagBackgroundRadius;

    public TagsViewController(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        tagsButton = findView(activity, R.id.tagsButton);

        final Resources res = tagsButton.getResources();
        tagBackgroundColor = res.getColor(R.color.bg_secondary);
        tagBackgroundRadius = res.getDimension(R.dimen.tag_radius);
        tagsButton.setOnClickListener(clickListener);
        tagsButton.setOnLongClickListener(longClickListener);
    }

    @Override public void showError(Throwable error) {
    }

    public void setTags(List<Tag> tags) {
        if (tags == null) {
            tags = Collections.emptyList();
        }

        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (Tag tag : tags) {
            ssb.append(tag.getTitle());
            ssb.setSpan(new TextBackgroundSpan(tagBackgroundColor, tagBackgroundRadius), ssb.length() - tag.getTitle().length(), ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.append(" ");
        }
        tagsButton.setText(ssb);
    }
}
