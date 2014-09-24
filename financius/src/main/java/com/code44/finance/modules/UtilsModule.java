package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.api.NetworkExecutor;
import com.code44.finance.qualifiers.ApplicationContext;
import com.code44.finance.qualifiers.Local;
import com.code44.finance.qualifiers.Network;
import com.code44.finance.utils.ActiveInterval;
import com.code44.finance.utils.Calculator;
import com.code44.finance.utils.CurrentInterval;
import com.code44.finance.utils.EventBus;
import com.code44.finance.utils.GeneralPrefs;
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

    @Provides public Calculator provideCalculator(@ApplicationContext Context context) {
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

    @Provides @Singleton public CurrentInterval provideCurrentInterval(@ApplicationContext Context context, EventBus eventBus, GeneralPrefs generalPrefs) {
        return new CurrentInterval(context, eventBus, generalPrefs.getIntervalType(), generalPrefs.getIntervalLength());
    }

    @Provides @Singleton public ActiveInterval provideActiveInterval(@ApplicationContext Context context, EventBus eventBus, GeneralPrefs generalPrefs) {
        return new ActiveInterval(context, eventBus, generalPrefs.getIntervalType(), generalPrefs.getIntervalLength());
    }

    @Provides public LayoutType provideLayoutType(@ApplicationContext Context context) {
        return new LayoutType(context);
    }
}
