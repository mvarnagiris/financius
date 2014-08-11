package com.code44.finance.modules.library;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.code44.finance.api.GcmRegistration;
import com.code44.finance.api.User;
import com.code44.finance.data.db.DBHelper;
import com.code44.finance.utils.GeneralPrefs;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
                ContextProvider.class,
                UtilProvider.class
        }
)
public class PersistenceProvider {
    @Provides @Singleton public DBHelper provideDBHelper(Context context) {
        return new DBHelper(context);
    }

    @Provides @Singleton public SQLiteDatabase provideSQLiteDatabase(DBHelper dbHelper) {
        return dbHelper.getWritableDatabase();
    }

    @Provides @Singleton public GcmRegistration provideGcmRegistration(Context context) {
        return new GcmRegistration(context);
    }

    @Provides @Singleton public User provideUser(Context context, DBHelper dbHelper, GcmRegistration gcmRegistration, Bus bus) {
        return new User(context, dbHelper, gcmRegistration, bus);
    }

    @Provides @Singleton public GeneralPrefs provideGeneralPrefs(Context context) {
        return new GeneralPrefs(context);
    }
}
