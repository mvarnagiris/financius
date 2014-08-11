package com.code44.finance.modules;

import com.code44.finance.App;
import com.code44.finance.modules.library.ContextProvider;
import com.code44.finance.modules.library.InjectorProvider;
import com.code44.finance.modules.library.PersistenceProvider;
import com.code44.finance.modules.library.UtilProvider;

import dagger.Module;

@Module(
        injects = {
                App.class,
        },
        includes = {
                ContextProvider.class,
                PersistenceProvider.class,
                UtilProvider.class,
                ApiProvider.class,
                CurrenciesApiProvider.class,
                InjectorProvider.class
        }
)
public class AppModule {
}
