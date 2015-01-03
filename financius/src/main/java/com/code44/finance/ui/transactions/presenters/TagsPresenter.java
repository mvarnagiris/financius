package com.code44.finance.ui.transactions.presenters;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.BaseActivity;
import com.code44.finance.ui.common.Presenter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteAdapter;
import com.code44.finance.ui.transactions.autocomplete.AutoCompleteResult;
import com.code44.finance.ui.transactions.autocomplete.adapters.AutoCompleteTagsAdapter;
import com.code44.finance.utils.TextBackgroundSpan;

import java.util.Collections;
import java.util.List;

public class TagsPresenter extends Presenter implements AutoCompletePresenter<List<Tag>>, AutoCompleteAdapter.AutoCompleteAdapterListener {
    private final Button tagsButton;
    private final View tagsDividerView;
    private final ViewGroup tagsAutoCompleteContainerView;

    private final int tagBackgroundColor;
    private final float tagBackgroundRadius;

    public TagsPresenter(BaseActivity activity, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        tagsButton = findView(activity, R.id.tagsButton);
        tagsDividerView = findView(activity, R.id.tagsDividerView);
        tagsAutoCompleteContainerView = findView(activity, R.id.tagsAutoCompleteContainerView);

        final Resources res = tagsButton.getResources();
        tagBackgroundColor = res.getColor(R.color.bg_secondary);
        tagBackgroundRadius = res.getDimension(R.dimen.tag_radius);
        tagsButton.setOnClickListener(clickListener);
        tagsButton.setOnLongClickListener(longClickListener);
    }

    @Override public void showError(Throwable error) {
    }

    @Override public void onAutoCompleteAdapterShown(AutoCompleteAdapter autoCompleteAdapter) {
        tagsButton.setHint(R.string.show_all);
        tagsDividerView.setVisibility(View.GONE);
    }

    @Override public void onAutoCompleteAdapterHidden(AutoCompleteAdapter autoCompleteAdapter) {
        tagsButton.setHint(R.string.tags_other);
        tagsDividerView.setVisibility(View.VISIBLE);
    }

    @Override public AutoCompleteAdapter<List<Tag>> showAutoComplete(AutoCompleteAdapter<?> currentAdapter, AutoCompleteResult autoCompleteResult, AutoCompleteAdapter.OnAutoCompleteItemClickListener<List<Tag>> clickListener, View view) {
        final AutoCompleteTagsAdapter adapter = new AutoCompleteTagsAdapter(tagsAutoCompleteContainerView, this, clickListener);
        if (adapter.show(currentAdapter, autoCompleteResult)) {
            return adapter;
        }
        return null;
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
