package com.code44.finance.modules;

import com.code44.finance.api.Request;
import com.code44.finance.modules.library.ContextProvider;
import com.code44.finance.modules.library.PersistenceProvider;

import dagger.Module;

@Module(
        injects = {
                Request.class
        },
        includes = {
                ContextProvider.class,
                PersistenceProvider.class
        }
)
public class RequestModule {
}
