package com.code44.finance.ui.tags.list;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.data.model.Tag;
import com.code44.finance.ui.common.activities.ModelsActivity;
import com.code44.finance.ui.common.adapters.ModelViewHolder;
import com.code44.finance.ui.common.adapters.ModelsAdapter;

class TagsAdapter extends ModelsAdapter<Tag, TagsAdapter.ViewHolder> {
    public TagsAdapter(@NonNull OnModelClickListener<Tag> onModelClickListener, @NonNull ModelsActivity.Mode mode) {
        super(onModelClickListener, mode);
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.li_tag, parent, false), this);
    }

    @Override protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Tag model, boolean isSelected) {
        holder.titleTextView.setText(model.getTitle());
        if (getMode() != ModelsActivity.Mode.View) {
            holder.selectCheckBox.setVisibility(View.VISIBLE);
            holder.selectCheckBox.setChecked(isSelected);
        } else {
            holder.selectCheckBox.setVisibility(View.GONE);
        }
    }

    @Override protected Tag modelFromCursor(Cursor cursor) {
        return Tag.from(cursor);
    }

    static class ViewHolder extends ModelViewHolder {
        private final CheckBox selectCheckBox;
        private final TextView titleTextView;

        public ViewHolder(@NonNull View itemView, @NonNull OnItemClickListener onItemClickListener) {
            super(itemView, onItemClickListener);
            selectCheckBox = (CheckBox) itemView.findViewById(R.id.selectCheckBox);
            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
        }
    }
}
