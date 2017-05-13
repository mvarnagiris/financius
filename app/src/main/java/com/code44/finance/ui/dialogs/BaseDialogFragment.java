package com.code44.finance.ui.dialogs;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.utils.EventBus;
import com.google.common.base.Strings;

import javax.inject.Inject;

public abstract class BaseDialogFragment extends DialogFragment {
    protected static final String ARG_REQUEST_CODE = "ARG_REQUEST_CODE";
    protected static final String ARG_TITLE = "ARG_TITLE";
    protected static final String ARG_ARGS = "ARG_ARGS";

    protected TextView titleView;

    protected int requestCode;

    @Inject EventBus eventBus;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.with(getActivity()).inject(this);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        titleView = (TextView) view.findViewById(R.id.titleTextView);

        // Setup
        requestCode = getArguments().getInt(ARG_REQUEST_CODE);
        if (titleView != null) {
            final String title = getArguments().getString(ARG_TITLE);
            if (Strings.isNullOrEmpty(title)) {
                titleView.setVisibility(View.GONE);
            } else {
                titleView.setText(title);
            }
        }
    }

    protected EventBus getEventBus() {
        return eventBus;
    }

    protected abstract static class Builder {
        protected final Bundle args;

        public Builder(int requestCode) {
            args = new Bundle();
            args.putInt(ARG_REQUEST_CODE, requestCode);
        }

        public Builder setTitle(String title) {
            args.putString(ARG_TITLE, title);
            return this;
        }

        public Builder setArgs(Bundle args) {
            args.putBundle(ARG_ARGS, args);
            return this;
        }

        public BaseDialogFragment build() {
            BaseDialogFragment dialogFragment = createFragment();
            dialogFragment.setArguments(args);
            return dialogFragment;
        }

        protected abstract BaseDialogFragment createFragment();
    }
}
