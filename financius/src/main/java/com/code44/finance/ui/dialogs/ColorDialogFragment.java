package com.code44.finance.ui.dialogs;

import android.content.Context;
import android.os.Parcel;

import com.code44.finance.R;
import com.code44.finance.adapters.ListDialogAdapter;

import java.util.ArrayList;
import java.util.List;

public class ColorDialogFragment extends ListDialogFragment {
    public static Builder build(int requestCode) {
        return new Builder(requestCode);
    }

    @Override protected ListDialogAdapter getAdapter() {
        final List<ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogItem(getString(R.string.select_color)));

        final int[] colors = getResources().getIntArray(R.array.category_colors);
        for (int color : colors) {
            items.add(new ColorListDialogItem(color));
        }
        return new ColorAdapter(getActivity(), items);
    }

    public static class ColorListDialogItem extends ListDialogItem {
        private int color;

        public ColorListDialogItem(int color) {
            super((String) null);
            this.color = color;
        }

        @Override public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(color);
        }

        public int getColor() {
            return color;
        }
    }

    public static class Builder extends ListDialogFragment.Builder {
        public Builder(int requestCode) {
            super(requestCode);
        }

        @Override protected BaseDialogFragment createFragment() {
            return new ColorDialogFragment();
        }
    }

    private static class ColorAdapter extends ListDialogAdapter {
        public ColorAdapter(Context context, List<ListDialogItem> items) {
            super(context, items);
        }

        @Override protected void bindView(ViewHolder holder, ListDialogItem item) {
            super.bindView(holder, item);

            if (item instanceof ColorListDialogItem) {
                holder.title_TV.setBackgroundColor(((ColorListDialogItem) item).getColor());
            } else {
                holder.title_TV.setBackground(null);
            }
        }
    }
}
