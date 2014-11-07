package com.code44.finance.ui.common;

public enum ViewBackgroundTheme {
    Light, Dark;

    public static ViewBackgroundTheme from(int value) {
        if (value == 1) {
            return Dark;
        }
        return Light;
    }
}
