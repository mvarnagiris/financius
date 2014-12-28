package com.code44.finance.ui.transactions.autocomplete;

import android.os.Parcel;
import android.os.Parcelable;

import com.code44.finance.data.model.Account;
import com.code44.finance.data.model.Category;
import com.code44.finance.data.model.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AutoCompleteResult implements Parcelable {
    public static final Parcelable.Creator<AutoCompleteResult> CREATOR = new Parcelable.Creator<AutoCompleteResult>() {
        public AutoCompleteResult createFromParcel(Parcel in) {
            return new AutoCompleteResult(in);
        }

        public AutoCompleteResult[] newArray(int size) {
            return new AutoCompleteResult[size];
        }
    };

    private List<Long> amounts;
    private List<Account> accountsFrom;
    private List<Account> accountsTo;
    private List<Category> categories;
    private List<List<Tag>> tags;
    private List<String> notes;

    public AutoCompleteResult() {
    }

    private AutoCompleteResult(Parcel in) {
        amounts = new ArrayList<>();
        in.readList(amounts, Long.class.getClassLoader());
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
        notes = new ArrayList<>();
        in.readStringList(notes);
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(amounts);
        dest.writeTypedList(accountsFrom);
        dest.writeTypedList(accountsTo);
        dest.writeTypedList(categories);
        dest.writeInt(tags.size());
        for (List<Tag> tagSubList : tags) {
            dest.writeTypedList(tagSubList);
        }
        dest.writeStringList(notes);
    }

    public List<Long> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Long> amounts) {
        this.amounts = amounts == null ? Collections.<Long>emptyList() : amounts;
    }

    public List<Account> getAccountsFrom() {
        return accountsFrom;
    }

    public void setAccountsFrom(List<Account> accountsFrom) {
        this.accountsFrom = accountsFrom == null ? Collections.<Account>emptyList() : accountsFrom;
    }

    public List<Account> getAccountsTo() {
        return accountsTo;
    }

    public void setAccountsTo(List<Account> accountsTo) {
        this.accountsTo = accountsTo == null ? Collections.<Account>emptyList() : accountsTo;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories == null ? Collections.<Category>emptyList() : categories;
    }

    public List<List<Tag>> getTags() {
        return tags;
    }

    public void setTags(List<List<Tag>> tags) {
        this.tags = tags == null ? Collections.<List<Tag>>emptyList() : tags;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes == null ? Collections.<String>emptyList() : notes;
    }
}
