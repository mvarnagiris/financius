package com.code44.finance.ui;


import android.app.Activity;
import android.app.Fragment;

import com.code44.finance.App;
import com.code44.finance.R;
import com.code44.finance.utils.EventBus;

import javax.inject.Inject;

public class BaseFragment extends Fragment {
    @Inject EventBus eventBus;

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        App.with(activity).inject(this);
    }

    public String getTitle() {
        return getString(R.string.app_name);
    }

    protected void requestTitleUpdate() {
        getEventBus().post(new RequestTitleUpdateEvent(getTitle()));
    }

    protected EventBus getEventBus() {
        return eventBus;
    }

    public static class RequestTitleUpdateEvent {
        private final String title;

        public RequestTitleUpdateEvent(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
