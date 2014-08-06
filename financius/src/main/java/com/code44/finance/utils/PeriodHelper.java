package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;

public class PeriodHelper extends Prefs {
    private static final String PREFIX = "period_helper_";

    private static PeriodHelper singleton;

    private PeriodHelper(Context context) {
        super(context);
    }

    public static synchronized PeriodHelper get() {
        if (singleton == null) {
            singleton = new PeriodHelper(App.getAppContext());
        }
        return singleton;
    }

    @Override
    protected String getPrefix() {
        return PREFIX;
    }

    public String getTitle() {
        return "August";
    }

    public static enum Type {
        DAY, WEEK, MONTH, YEAR, CUSTOM
    }
}
