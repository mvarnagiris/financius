package com.code44.finance;

import com.code44.finance.modules.AppModule;

final class Modules {
    private Modules() {
        // No instances.
    }

    static Object[] list(App app) {
        return new Object[]{
                new AppModule(app)
        };
    }
}