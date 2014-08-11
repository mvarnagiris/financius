package com.code44.finance.modules.library;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class ContextProvider {
    private final Context context;

    public ContextProvider(Context context) {
        this.context = context;
    }

    @Provides public Context provideContext() {
        return context;
    }
}
