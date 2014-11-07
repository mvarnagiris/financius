package com.code44.finance.modules.providers;

import android.content.Context;

import com.code44.finance.common.utils.Preconditions;
import com.code44.finance.qualifiers.ApplicationContext;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class ContextProviderModule {
    private final Context context;

    public ContextProviderModule(Context context) {
        this.context = Preconditions.notNull(context, "Context cannot be null.");
    }

    @Provides @ApplicationContext Context provideApplicationContext() {
        return context;
    }
}
