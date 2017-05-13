package com.code44.finance;

final class Modules {
    private Modules() {
        // No instances.
    }

    static Object[] list(App app) {
        return new Object[]{new AppModule(app)};
    }
}