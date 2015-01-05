package com.code44.finance.ui.tags;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.adapters.ModelsAdapter;
import com.code44.finance.ui.common.presenters.ModelsPresenter;

public class TagsAdapter extends ModelsAdapter<Tag> {
    public TagsAdapter(OnModelClickListener<Tag> onModelClickListener) {
        super(onModelClickListener);
    }

    @Override public ViewHolder createModelViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_tag, parent, false));
    }

    @Override protected Tag modelFromCursor(Cursor cursor) {
        return Tag.from(cursor);
    }

    private static class ViewHolder extends ModelsAdapter.ViewHolder<Tag> {
        private final CheckBox selectCheckBox;
        private final TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            // Get views
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.selectCheckBox);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }

        @Override protected void bind(Tag tag, Cursor cursor, int position, ModelsPresenter.Mode mode, boolean isSelected) {
            titleTextView.setText(tag.getTitle());
            if (mode != ModelsPresenter.Mode.View) {
                selectCheckBox.setChecked(isSelected);
            }
        }

    }
}
