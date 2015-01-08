package com.code44.finance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.ui.dialogs.ListDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ListDialogAdapter extends BaseAdapter {
    private final Context context;
    private final List<ListDialogFragment.ListDialogItem> items;

    public ListDialogAdapter(Context context, List<ListDialogFragment.ListDialogItem> items) {
        Preconditions.notNull(context, "Context cannot be null.");
        Preconditions.notNull(items, "Items cannot be null.");

        this.context = context;
        this.items = items;
    }

    @Override public int getCount() {
        return items.size();
    }

    @Override public ListDialogFragment.ListDialogItem getItem(int position) {
        return items.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = newView(context, parent);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ListDialogFragment.ListDialogItem item = items.get(position);
        bindView(holder, item);

        return convertView;
    }

    protected View newView(Context context, ViewGroup parent) {
        final int layoutResId;
        if (isMultipleChoice()) {
            layoutResId = R.layout.li_dialog_multiple_choice;
        } else if (isSelectable()) {
            layoutResId = R.layout.li_dialog_single_choice;
        } else {
            layoutResId = R.layout.li_dialog_simple;
        }

        return LayoutInflater.from(context).inflate(layoutResId, parent, false);
    }

    protected void bindView(ViewHolder holder, ListDialogFragment.ListDialogItem item) {
        holder.title_TV.setText(item.getTitle());

        if (isSelectable()) {
            final ListDialogFragment.SingleChoiceListDialogItem singleChoiceItem = (ListDialogFragment.SingleChoiceListDialogItem) item;
            ((Checkable) holder.title_TV).setChecked(singleChoiceItem.isSelected());
        }
    }

    public void onPositionClicked(int position) {
        if (isSelectable()) {
            ListDialogFragment.SingleChoiceListDialogItem selectableItem = (ListDialogFragment.SingleChoiceListDialogItem) items.get(position);

            if (isMultipleChoice()) {
                selectableItem.setSelected(!selectableItem.isSelected());
            } else if (!selectableItem.isSelected()) {
                for (ListDialogFragment.ListDialogItem item : items) {
                    ((ListDialogFragment.SingleChoiceListDialogItem) item).setSelected(false);
                }
                selectableItem.setSelected(true);
            }

            notifyDataSetChanged();
        }
    }

    public List<Integer> getSelectedPositions() {
        final List<Integer> selectedPositions = new ArrayList<>();
        if (items.size() > 0 && items.get(0) instanceof ListDialogFragment.SingleChoiceListDialogItem) {
            for (int i = 0, size = items.size(); i < size; i++) {
                if (((ListDialogFragment.SingleChoiceListDialogItem) items.get(0)).isSelected()) {
                    selectedPositions.add(i);
                }
            }
        }
        return selectedPositions;
    }

    private boolean isSelectable() {
        return items.get(0) instanceof ListDialogFragment.SingleChoiceListDialogItem;
    }

    private boolean isMultipleChoice() {
        return items.get(0) instanceof ListDialogFragment.MultipleChoiceListDialogItem;
    }

    protected static class ViewHolder {
        public final TextView title_TV;

        public ViewHolder(View view) {
            title_TV = (TextView) view.findViewById(R.id.titleTextView);
        }
    }
}
