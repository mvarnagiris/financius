package com.code44.finance.ui;


import android.app.Fragment;

import com.code44.finance.R;
import com.code44.finance.api.User;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.IntervalHelper;

public class BaseFragment extends Fragment {
    protected final EventBus eventBus = EventBus.get();
    protected final IntervalHelper intervalHelper = IntervalHelper.get();
    protected final User user = User.get();
    protected final GeneralPrefs generalPrefs = GeneralPrefs.get();

    public String getTitle() {
        return getString(R.string.app_name);
    }

    protected void requestTitleUpdate() {
        getEventBus().post(new RequestTitleUpdateEvent(getTitle()));
    }

    protected EventBus getEventBus() {
        return eventBus;
    }

    protected IntervalHelper getIntervalHelper() {
        return intervalHelper;
    }

    protected User getUser() {
        return user;
    }

    protected GeneralPrefs getGeneralPrefs() {
        return generalPrefs;
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
