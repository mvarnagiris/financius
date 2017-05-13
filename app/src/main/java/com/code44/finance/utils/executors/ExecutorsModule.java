package com.code44.finance.utils.executors;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false)
public final class ExecutorsModule {
    @Provides @Singleton @Network public ExecutorService provideNetworkExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new NetworkExecutor(numberCores * 2 + 1);
    }

    @Provides @Singleton @Local public ExecutorService provideLocalExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new LocalExecutor(numberCores * 2 + 1);
    }
}
