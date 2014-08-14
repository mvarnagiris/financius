package com.code44.finance.ui;


import android.app.Fragment;

import com.code44.finance.R;
import com.code44.finance.utils.EventBus;

public class BaseFragment extends Fragment {
    protected final EventBus eventBus = EventBus.get();

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
