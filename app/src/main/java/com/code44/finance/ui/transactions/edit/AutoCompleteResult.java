package com.code44.finance.ui.transactions.edit;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

final class AutoCompleteResult implements Parcelable {
    public static final Creator<AutoCompleteResult> CREATOR = new Creator<AutoCompleteResult>() {
        public AutoCompleteResult createFromParcel(Parcel in) {
            return new AutoCompleteResult(in);
        }

        public AutoCompleteResult[] newArray(int size) {
            return new AutoCompleteResult[size];
        }
    };

    private final List<Account> accountsFrom;
    private final List<Account> accountsTo;
    private final List<Category> categories;
    private final List<List<Tag>> tags;

    public AutoCompleteResult(@NonNull List<Account> accountsFrom, @NonNull List<Account> accountsTo, @NonNull List<Category> categories, @NonNull List<List<Tag>> tags) {
        this.accountsFrom = checkNotNull(accountsFrom, "Accounts from List cannot be null.");
        this.accountsTo = checkNotNull(accountsTo, "Accounts to List cannot be null.");
        this.categories = checkNotNull(categories, "Categories List cannot be null.");
        this.tags = checkNotNull(tags, "Tags List cannot be null.");
    }

    private AutoCompleteResult(@NonNull Parcel in) {
        accountsFrom = new ArrayList<>();
        in.readTypedList(accountsFrom, Account.CREATOR);
        accountsTo = new ArrayList<>();
        in.readTypedList(accountsTo, Account.CREATOR);
        categories = new ArrayList<>();
        in.readTypedList(categories, Category.CREATOR);
        tags = new ArrayList<>();
        for (int i = 0, size = in.readInt(); i < size; i++) {
            final List<Tag> subList = new ArrayList<>();
            in.readTypedList(subList, Tag.CREATOR);
            tags.add(subList);
        }
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(accountsFrom);
        dest.writeTypedList(accountsTo);
        dest.writeTypedList(categories);
        dest.writeInt(tags.size());
        for (List<Tag> tagSubList : tags) {
            dest.writeTypedList(tagSubList);
        }
    }

    @NonNull public List<Account> getAccountsFrom() {
        return accountsFrom;
    }

    @NonNull public List<Account> getAccountsTo() {
        return accountsTo;
    }

    @NonNull public List<Category> getCategories() {
        return categories;
    }

    @NonNull public List<List<Tag>> getTags() {
        return tags;
    }
}
