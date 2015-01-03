package com.code44.finance.ui.common.navigation;

public enum NavigationScreen {
    User(1), Overview(2), Accounts(3), Transactions(4), Reports(5), Settings(6);

    private final int id;

    NavigationScreen(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
