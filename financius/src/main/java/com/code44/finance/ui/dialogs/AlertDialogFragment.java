package com.code44.finance.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code44.finance.R;
import com.code44.finance.common.utils.StringUtils;

public class AlertDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    protected static final String ARG_MESSAGE = "ARG_MESSAGE";
    protected static final String ARG_POSITIVE_BUTTON_TEXT = "ARG_POSITIVE_BUTTON_TEXT";
    protected static final String ARG_NEGATIVE_BUTTON_TEXT = "ARG_NEGATIVE_BUTTON_TEXT";
    protected static final String ARG_POSITIVE_BUTTON_COLOR = "ARG_POSITIVE_BUTTON_COLOR";
    protected static final String ARG_NEGATIVE_BUTTON_COLOR = "ARG_NEGATIVE_BUTTON_COLOR";

    protected TextView message_TV;
    protected Button positive_B;
    protected Button negative_B;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_alert, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        message_TV = (TextView) view.findViewById(R.id.message_TV);
        positive_B = (Button) view.findViewById(R.id.positive_B);
        negative_B = (Button) view.findViewById(R.id.negative_B);

        // Setup
        final String message = getArguments().getString(ARG_MESSAGE);
        final String positiveButtonTitle = getArguments().getString(ARG_POSITIVE_BUTTON_TEXT);
        final String negativeButtonTitle = getArguments().getString(ARG_NEGATIVE_BUTTON_TEXT);
        final int positiveButtonColor = getArguments().getInt(ARG_POSITIVE_BUTTON_COLOR, getResources().getColor(R.color.text_primary));
        final int negativeButtonColor = getArguments().getInt(ARG_NEGATIVE_BUTTON_COLOR, getResources().getColor(R.color.text_primary));

        if (StringUtils.isEmpty(message)) {
            message_TV.setVisibility(View.GONE);
        } else {
            message_TV.setText(message);
        }

        if (StringUtils.isEmpty(positiveButtonTitle)) {
            positive_B.setVisibility(View.GONE);
        } else {
            positive_B.setText(positiveButtonTitle);
            positive_B.setTextColor(positiveButtonColor);
            positive_B.setOnClickListener(this);
        }

        if (StringUtils.isEmpty(negativeButtonTitle)) {
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

    protected void onClickPositive() {
        getEventBus().post(createEvent(requestCode, true));
        dismiss();
    }

    protected void onClickNegative() {
        getEventBus().post(createEvent(requestCode, false));
        dismiss();
    }

    protected AlertDialogEvent createEvent(int requestCode, boolean isPositiveClicked) {
        return new AlertDialogEvent(requestCode, isPositiveClicked);
    }

    public static class AlertDialogEvent {
        private final int requestCode;
        private final boolean isPositiveClicked;

        public AlertDialogEvent(int requestCode, boolean isPositiveClicked) {
            this.requestCode = requestCode;
            this.isPositiveClicked = isPositiveClicked;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public boolean isPositiveClicked() {
            return isPositiveClicked;
        }
    }

    public static class Builder extends BaseDialogFragment.Builder {

        public Builder(int requestCode) {
            super(requestCode);
        }

        @Override public Builder setTitle(String title) {
            return (Builder) super.setTitle(title);
        }

        @Override public Builder setArgs(Bundle args) {
            return (Builder) super.setArgs(args);
        }

        public Builder setMessage(String message) {
            args.putString(ARG_MESSAGE, message);
            return this;
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

        @Override protected BaseDialogFragment createFragment() {
            return new AlertDialogFragment();
        }
    }
}
