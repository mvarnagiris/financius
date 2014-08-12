package com.code44.finance.utils;

import android.content.Context;

import com.code44.finance.App;

public class PeriodHelper extends Prefs {
    private static final String PREFIX = "period_helper_";

    public PeriodHelper(Context context) {
        super(context);
    }

    public static PeriodHelper get() {
        return App.getPeriodHelper();
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
