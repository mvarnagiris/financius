package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.api.NetworkExecutor;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.qualifiers.Network;
import com.code44.finance.utils.Calculator;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.IntervalHelper;
import com.code44.finance.utils.LayoutType;
import com.code44.finance.utils.LocalExecutor;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false
)
public final class UtilsModule {
    @Provides @Singleton public EventBus providesEventBus() {
        return new EventBus();
    }

    @Provides @Singleton public Calculator provideCalculator(@ApplicationContext Context context) {
        return new Calculator(context);
    }

    @Provides @Singleton @Network public Executor provideNetworkExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new NetworkExecutor(numberCores * 2 + 1);
    }

    @Provides @Singleton @Local public Executor provideLocalExecutor() {
        final int numberCores = Runtime.getRuntime().availableProcessors();
        return new LocalExecutor(numberCores * 2 + 1);
    }

    @Provides @Singleton public IntervalHelper provideIntervalHelper(@ApplicationContext Context context, EventBus eventBus) {
        return new IntervalHelper(context, eventBus);
    }

    @Provides public LayoutType provideLayoutType(@ApplicationContext Context context) {
        return new LayoutType(context);
    }
}
