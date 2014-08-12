package com.code44.finance.modules.library;

import android.content.Context;

import com.code44.finance.qualifiers.ForNetwork;
import com.code44.finance.utils.PeriodHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
                ContextProvider.class
        }
)
public class UtilProvider {
    @Provides @Singleton public Bus provideBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides @Singleton public PeriodHelper providePeriodHelper(Context context) {
        return new PeriodHelper(context);
    }

    @Provides @Singleton @ForNetwork public Executor provideNetworkExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(numberCores * 2 + 1);
    }
}
