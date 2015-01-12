package com.code44.finance.ui.accounts.detail;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Pair;

import com.code44.finance.data.model.Account;
import com.code44.finance.ui.common.presenters.ModelActivityPresenter;
import com.code44.finance.utils.EventBus;

public class AccountActivityPresenter extends ModelActivityPresenter<Account> {
    protected AccountActivityPresenter(EventBus eventBus) {
        super(eventBus);
    }

    @Override protected CursorLoader getModelCursorLoader(Context context, String modelId) {
        return null;
    }

    @Override protected Account getModelFrom(Cursor cursor) {
        return null;
    }

    @Override protected void onModelLoaded(Account model) {

    }

    @Override protected void startModelEdit(Context context, String modelId) {

    }

    @Override protected Uri getDeleteUri() {
        return null;
    }

    @Override protected Pair<String, String[]> getDeleteSelection(String modelId) {
        return null;
    }
}
