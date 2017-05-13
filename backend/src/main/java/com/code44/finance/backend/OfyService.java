package com.code44.finance.backend;

import com.code44.finance.backend.entities.AccountEntity;
import com.code44.finance.backend.entities.CategoryEntity;
import com.code44.finance.backend.entities.ConfigEntity;
import com.code44.finance.backend.entities.CurrencyEntity;
import com.code44.finance.backend.entities.DeviceEntity;
import com.code44.finance.backend.entities.TagEntity;
import com.code44.finance.backend.entities.TransactionEntity;
import com.code44.finance.backend.entities.UserEntity;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 */
public class OfyService {
    private static final Logger log = Logger.getLogger(OfyService.class.getName());

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }

    static {
        try {
            ObjectifyService.register(UserEntity.class);
            ObjectifyService.register(DeviceEntity.class);
            ObjectifyService.register(CurrencyEntity.class);
            ObjectifyService.register(TagEntity.class);
            ObjectifyService.register(CategoryEntity.class);
            ObjectifyService.register(AccountEntity.class);
            ObjectifyService.register(TransactionEntity.class);
            ObjectifyService.register(ConfigEntity.class);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Registering entities failed.", e);
            throw e;
        }
    }
}
