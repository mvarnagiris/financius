package com.code44.finance.modules;

import android.content.Context;

import com.code44.finance.App;
import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.data.Query;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.ui.overview.OverviewFragment;
import com.code44.finance.utils.GeneralPrefs;
import com.code44.finance.utils.LayoutType;
import com.code44.finance.utils.PeriodHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = {
                OverviewFragment.class,
                Query.class,
                App.class,
                Context.class,
                DBHelper.class,
                Bus.class,
                GcmRegistration.class,
                User.class,
                PeriodHelper.class,
                GeneralPrefs.class,
                LayoutType.class
        },
        includes = {
                ApiProvider.class,
                CurrenciesApiProvider.class
        }
)
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton public Context provideAppContext() {
        return context;
    }

    @Provides @Singleton public DBHelper provideDBHelper(Context context) {
        return new DBHelper(context);
    }

    @Provides @Singleton public Bus provideBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides @Singleton public GcmRegistration provideGcmRegistration(Context context) {
        return new GcmRegistration(context, new GcmRegistration.DefaultAppVersionProvider());
    }

    @Provides @Singleton public User provideUser(Context context, DBHelper dbHelper, GcmRegistration gcmRegistration) {
        return new User(context);
    }

    @Provides @Singleton public PeriodHelper providePeriodHelper(Context context) {
        return new PeriodHelper(context);
    }

    @Provides @Singleton public LayoutType provideLayoutType(Context context) {
        return new LayoutType(context);
    }

    @Provides @Singleton public GeneralPrefs provideGeneralPrefs(Context context) {
        return new GeneralPrefs(context);
    }
}
