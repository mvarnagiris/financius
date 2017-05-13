package com.code44.finance.ui.dialogs;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.code44.finance.R;
import com.code44.finance.utils.ThemeUtils;
import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListDialogFragment extends BaseDialogFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    protected static final String ARG_ITEMS = "ARG_ITEMS";
    protected static final String ARG_POSITIVE_BUTTON_TEXT = "ARG_POSITIVE_BUTTON_TEXT";
    protected static final String ARG_NEGATIVE_BUTTON_TEXT = "ARG_NEGATIVE_BUTTON_TEXT";
    protected static final String ARG_POSITIVE_BUTTON_COLOR = "ARG_POSITIVE_BUTTON_COLOR";
    protected static final String ARG_NEGATIVE_BUTTON_COLOR = "ARG_NEGATIVE_BUTTON_COLOR";

    protected ListView list_V;
    protected Button positive_B;
    protected Button negative_B;

    protected ListDialogAdapter adapter;

    public static Builder build(int requestCode) {
        return new Builder(requestCode);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        list_V = (ListView) view.findViewById(R.id.listView);
        positive_B = (Button) view.findViewById(R.id.positive_B);
        negative_B = (Button) view.findViewById(R.id.negative_B);

        // Setup
        final String positiveButtonTitle = getArguments().getString(ARG_POSITIVE_BUTTON_TEXT);
        final String negativeButtonTitle = getArguments().getString(ARG_NEGATIVE_BUTTON_TEXT);
        final int positiveButtonColor = getArguments().getInt(ARG_POSITIVE_BUTTON_COLOR, ThemeUtils.getColor(getActivity(), android.R.attr.textColorPrimary));
        final int negativeButtonColor = getArguments().getInt(ARG_NEGATIVE_BUTTON_COLOR, ThemeUtils.getColor(getActivity(), android.R.attr.textColorPrimary));

        adapter = getAdapter();
        list_V.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        list_V.setAdapter(adapter);
        list_V.setOnItemClickListener(this);

        if (Strings.isNullOrEmpty(positiveButtonTitle)) {
            positive_B.setVisibility(View.GONE);
        } else {
            positive_B.setText(positiveButtonTitle);
            positive_B.setTextColor(positiveButtonColor);
            positive_B.setOnClickListener(this);
        }

        if (Strings.isNullOrEmpty(negativeButtonTitle)) {
            negative_B.setVisibility(View.GONE);
        } else {
            negative_B.setText(negativeButtonTitle);
            negative_B.setTextColor(negativeButtonColor);
            negative_B.setOnClickListener(this);
        }
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.positive_B:
                onClickPositive();
                break;

            case R.id.negative_B:
                onClickNegative();
                break;
        }
    }

    @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        adapter.onPositionClicked(position);
        getEventBus().post(createEvent(requestCode, true, position));
    }

    protected ListDialogAdapter getAdapter() {
        final ArrayList<ListDialogItem> items = getArguments().getParcelableArrayList(ARG_ITEMS);
        return new ListDialogAdapter(getActivity(), items);
    }

    protected void onClickPositive() {
        getEventBus().post(createEvent(requestCode, true, -1));
        dismiss();
    }

    protected void onClickNegative() {
        getEventBus().post(createEvent(requestCode, false, -1));
        dismiss();
    }

    protected ListDialogEvent createEvent(int requestCode, boolean isPositiveClicked, int position) {
        return new ListDialogEvent(this, adapter, requestCode, isPositiveClicked, position, adapter.getSelectedPositions(), getArguments().getBundle(ARG_ARGS));
    }

    public static class ListDialogEvent {
        private final ListDialogFragment dialogFragment;
        private final ListDialogAdapter adapter;
        private final int requestCode;
        private final boolean isPositiveClicked;
        private final int position;
        private final List<Integer> selectedPositions;
        private final Bundle args;

        public ListDialogEvent(ListDialogFragment dialogFragment, ListDialogAdapter adapter, int requestCode, boolean isPositiveClicked, int position, List<Integer> selectedPositions, Bundle args) {
            this.dialogFragment = dialogFragment;
            this.adapter = adapter;
            this.requestCode = requestCode;
            this.isPositiveClicked = isPositiveClicked;
            this.position = position;
            this.selectedPositions = selectedPositions;
            this.args = args;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public ListDialogAdapter getAdapter() {
            return adapter;
        }

        public boolean isPositiveClicked() {
            return isPositiveClicked;
        }

        public int getPosition() {
            return position;
        }

        public List<Integer> getSelectedPositions() {
            return selectedPositions;
        }

        public boolean isActionButtonClicked() {
            return position < 0;
        }

        public void dismiss() {
            if (dialogFragment != null) {
                dialogFragment.dismiss();
            }
        }
    }

    public static class ListDialogItem implements Parcelable {
        public static final Creator<ListDialogItem> CREATOR = new Creator<ListDialogItem>() {
            public ListDialogItem createFromParcel(Parcel in) {
                return new ListDialogItem(in);
            }

            public ListDialogItem[] newArray(int size) {
                return new ListDialogItem[size];
            }
        };

        private String title;

        public ListDialogItem(String title) {
            this.title = title;
        }

        public ListDialogItem(Parcel parcel) {
            setTitle(parcel.readString());
        }

        @Override public int describeContents() {
            return 0;
        }

        @Override public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(title);
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class SingleChoiceListDialogItem extends ListDialogItem {
        public static final Creator<SingleChoiceListDialogItem> CREATOR = new Creator<SingleChoiceListDialogItem>() {
            public SingleChoiceListDialogItem createFromParcel(Parcel in) {
                return new SingleChoiceListDialogItem(in);
            }

            public SingleChoiceListDialogItem[] newArray(int size) {
                return new SingleChoiceListDialogItem[size];
            }
        };

        private boolean isSelected;

        public SingleChoiceListDialogItem(String title, boolean isSelected) {
            super(title);
            this.isSelected = isSelected;
        }

        public SingleChoiceListDialogItem(Parcel parcel) {
            super(parcel);
            setSelected(parcel.readInt() != 0);
        }

        @Override public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(isSelected ? 1 : 0);
        }

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean isSelected) {
            this.isSelected = isSelected;
        }
    }

    public static class MultipleChoiceListDialogItem extends SingleChoiceListDialogItem {
        public static final Creator<MultipleChoiceListDialogItem> CREATOR = new Creator<MultipleChoiceListDialogItem>() {
            public MultipleChoiceListDialogItem createFromParcel(Parcel in) {
                return new MultipleChoiceListDialogItem(in);
            }

            public MultipleChoiceListDialogItem[] newArray(int size) {
                return new MultipleChoiceListDialogItem[size];
            }
        };

        public MultipleChoiceListDialogItem(String title, boolean isSelected) {
            super(title, isSelected);
        }

        public MultipleChoiceListDialogItem(Parcel parcel) {
            super(parcel);
        }
    }

    public static class Builder extends BaseDialogFragment.Builder {
        private List<ListDialogItem> items;

        public Builder(int requestCode) {
            super(requestCode);
        }

        @Override public Builder setTitle(String title) {
            return (Builder) super.setTitle(title);
        }

        @Override public Builder setArgs(Bundle args) {
            return (Builder) super.setArgs(args);
        }

        @Override protected BaseDialogFragment createFragment() {
            if (items == null) {
                items = Collections.emptyList();
            }

            args.putParcelableArrayList(ARG_ITEMS, new ArrayList<Parcelable>(items));

            return new ListDialogFragment();
        }

        public Builder setPositiveButtonText(String positiveButtonText) {
            args.putString(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText);
            return this;
        }

        public Builder setNegativeButtonText(String negativeButtonText) {
            args.putString(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText);
            return this;
        }

        public Builder setPositiveButtonColor(int positiveButtonColor) {
            args.putInt(ARG_POSITIVE_BUTTON_COLOR, positiveButtonColor);
            return this;
        }

        public Builder setNegativeButtonColor(int negativeButtonColor) {
            args.putInt(ARG_NEGATIVE_BUTTON_COLOR, negativeButtonColor);
            return this;
        }

        public Builder setItems(List<ListDialogItem> items) {
            this.items = items;
            return this;
        }
    }
}
