package com.code44.finance.modules.library;

import com.code44.finance.utils.Injector;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true
)
public class InjectorProvider {
    private final Injector injector;

    public InjectorProvider(Injector injector) {
        this.injector = injector;
    }

    @Provides
    public Injector provideInjector() {
        return injector;
    }
}
